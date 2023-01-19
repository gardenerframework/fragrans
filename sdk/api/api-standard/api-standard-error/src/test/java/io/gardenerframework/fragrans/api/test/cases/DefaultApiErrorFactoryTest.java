package io.gardenerframework.fragrans.api.test.cases;

import io.gardenerframework.fragrans.api.standard.error.ApiErrorFactory;
import io.gardenerframework.fragrans.api.standard.error.DefaultApiErrorConstants;
import io.gardenerframework.fragrans.api.standard.error.exception.ApiErrorDetailsSupplier;
import io.gardenerframework.fragrans.api.standard.error.exception.ApiErrorWrappingException;
import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.client.BadRequestException;
import io.gardenerframework.fragrans.api.standard.error.exception.client.NotAcceptableException;
import io.gardenerframework.fragrans.api.standard.error.exception.server.InternalServerErrorException;
import io.gardenerframework.fragrans.api.standard.schema.ApiError;
import io.gardenerframework.fragrans.api.test.ApiStandardErrorTestApplication;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ClassUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * @author zhanghan30
 * @date 2022/5/9 10:27 下午
 */
@SpringBootTest(classes = ApiStandardErrorTestApplication.class)
@DisplayName("默认ApiErrorFactory测试")
public class DefaultApiErrorFactoryTest {
    @Autowired
    private EnhancedMessageSource messageSource;
    @Autowired
    private ApiErrorFactory factory;

    @Test
    @DisplayName("DomainErrorPackage测试")
    public void domainErrorPackageTest() throws Exception {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("path", "/test");
        errorAttributes.put("status", 999);
        String[] packages = new String[]{
                ClassUtils.getPackageName(BadRequestException.class),
                ClassUtils.getPackageName(InternalServerErrorException.class)
        };
        for (String _package : packages) {
            Set<Class<? extends ApiStandardExceptions.ApiStandardException>> classes = new HashSet<>(new Reflections(_package, new SubTypesScanner(false))
                    .getSubTypesOf(ApiStandardExceptions.ApiStandardException.class));
            for (Class<? extends ApiStandardExceptions.ApiStandardException> clazz : classes) {
                if (ApiStandardExceptions.ClientSideException.class.equals(clazz) || ApiStandardExceptions.ServerSideException.class.equals(clazz)) {
                    continue;
                }
                ApiError apiError = factory.createApiError(errorAttributes, clazz.newInstance(), Locale.getDefault());
                //状态码会被ResponseStatusAnnotationAwareListener覆盖
                Assertions.assertEquals(Objects.requireNonNull(AnnotationUtils.findAnnotation(clazz, ResponseStatus.class)).value().value(), apiError.getStatus());
                Assertions.assertEquals(Objects.requireNonNull(AnnotationUtils.findAnnotation(clazz, ResponseStatus.class)).value().getReasonPhrase(), apiError.getReason());
                Assertions.assertEquals(messageSource.getMessage(clazz, Locale.getDefault()), apiError.getMessage());
            }
        }
        //非领域内的包基本就没人管
        ApiError apiError = factory.createApiError(errorAttributes, new RuntimeException(), Locale.getDefault());
        Assertions.assertEquals(errorAttributes.get("status"), apiError.getStatus());
        Assertions.assertEquals(DefaultApiErrorConstants.GENERIC_ERROR, apiError.getError());
        Assertions.assertEquals(messageSource.getMessage(DefaultApiErrorConstants.GENERIC_ERROR, new Object[0], Locale.getDefault()), apiError.getMessage());
    }

    @Test
    @DisplayName("null error 测试")
    public void nullErrorTest() {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("path", "/test");
        errorAttributes.put("status", 400);
        //这种sendError过来的东西会被转为对应的状态码的异常
        ApiError apiError = factory.createApiError(errorAttributes, null, Locale.getDefault());
        Assertions.assertEquals(BadRequestException.class.getCanonicalName(), apiError.getError());
        Assertions.assertEquals(messageSource.getMessage(BadRequestException.class, Locale.getDefault()), apiError.getMessage());
    }

    @Test
    @DisplayName("常见的spring异常转换 测试")
    public void simpleCommonSpringExceptionTest() {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("path", "/test");
        errorAttributes.put("status", 666);
        ApiError apiError = factory.createApiError(errorAttributes, new HttpMediaTypeNotAcceptableException(""), Locale.getDefault());
        Assertions.assertEquals(NotAcceptableException.class.getCanonicalName(), apiError.getError());
        Assertions.assertEquals(messageSource.getMessage(NotAcceptableException.class, Locale.getDefault()), apiError.getMessage());
    }

    @Test
    @DisplayName("ResponseStatusException测试")
    public void responseStatusExceptionTest() {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("path", "/test");
        errorAttributes.put("status", 666);
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_GATEWAY, null, new RuntimeException(""));
        ApiError apiError = factory.createApiError(errorAttributes, exception, Locale.getDefault());
        Assertions.assertEquals(exception.getStatus().value(), apiError.getStatus());
        Assertions.assertEquals(exception.getStatus().getReasonPhrase(), apiError.getReason());
        Assertions.assertEquals(DefaultApiErrorConstants.GENERIC_ERROR, apiError.getError());
        Assertions.assertEquals(messageSource.getMessage(DefaultApiErrorConstants.GENERIC_ERROR, new Object[0], Locale.getDefault()), apiError.getMessage());

        exception = new ResponseStatusException(HttpStatus.BAD_GATEWAY, null, new BadRequestException());
        apiError = factory.createApiError(errorAttributes, exception, Locale.getDefault());
        Assertions.assertEquals(exception.getStatus().getReasonPhrase(), apiError.getReason());
        Assertions.assertEquals(BadRequestException.class.getCanonicalName(), apiError.getError());
        Assertions.assertEquals(messageSource.getMessage(BadRequestException.class, null, Locale.getDefault()), apiError.getMessage());

    }

    @Test
    @DisplayName("ApiErrorWrappingException测试")
    public void apiErrorWrappingExceptionTest() {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("path", "/test");
        errorAttributes.put("status", 666);
        ApiErrorWrappingException exception = new ApiErrorWrappingException(UUID.randomUUID().toString(), HttpStatus.INSUFFICIENT_STORAGE, UUID.randomUUID().toString(), new HashMap<>());
        ApiError apiError = factory.createApiError(errorAttributes, exception, Locale.getDefault());
        Assertions.assertEquals(exception.getStatus().value(), apiError.getStatus());
        Assertions.assertEquals(exception.getMessage(), apiError.getMessage());
        Assertions.assertEquals(exception.getError(), apiError.getError());
        Assertions.assertNotNull(apiError.getDetails());
    }

    @Test
    @DisplayName("ApiErrorDetailsSupplier测试")
    public void apiErrorDetailsSupplierTest() {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("path", "/test");
        errorAttributes.put("status", 666);
        Map<String, Object> details = new HashMap<>();
        details.put("test", UUID.randomUUID().toString());
        ApiError apiError = factory.createApiError(errorAttributes, new ApiErrorDetailsSupplier() {
            @Override
            public Map<String, Object> getDetails() {

                return details;
            }
        }, Locale.getDefault());
        Assertions.assertEquals(details, apiError.getDetails());
    }

    @Test
    @DisplayName("DomainErrorSuperClass测试")
    public void domainErrorSuperClassTest() {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("path", "/test");
        errorAttributes.put("status", 666);
        Map<String, Object> details = new HashMap<>();
        details.put("test", UUID.randomUUID().toString());
        ApiError apiError = factory.createApiError(errorAttributes, new SubClass(), Locale.getDefault());
        Assertions.assertEquals(SubClass.class.getCanonicalName(), apiError.getError());
        apiError = factory.createApiError(errorAttributes, new SuperClass(), Locale.getDefault());
        Assertions.assertEquals(SuperClass.class.getCanonicalName(), apiError.getError());
    }


    public static class SuperClass {

    }

    public static class SubClass extends SuperClass {

    }
}
