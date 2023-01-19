package io.gardenerframework.fragrans.api.test.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.fragrans.api.standard.schema.ApiError;
import io.gardenerframework.fragrans.api.test.cases.exception.ResponseStatusAnnotatedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.ReflectionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ServletApiErrorAttributesTest {
    private Logger logger = LoggerFactory.getLogger(ServletApiErrorAttributesTest.class);
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void doTest(String uri, boolean isPost) throws JsonProcessingException {
        String[] packages = new String[]{"io.gardenerframework.fragrans.api.exception.client", "io.gardenerframework.fragrans.api.exception.server"};
        for (String _package : packages) {
            List<Class<?>> allClassesInPackage = ReflectionSupport.findAllClassesInPackage(
                    _package,
                    Exception.class::isAssignableFrom, new Predicate<String>() {
                        @Override
                        public boolean test(String s) {
                            try {
                                return Exception.class.isAssignableFrom(Class.forName(s));
                            } catch (ClassNotFoundException e) {
                                return false;
                            }
                        }
                    });
            for (Class<?> clazz : allClassesInPackage) {
                try {
                    logger.debug("testing {}", clazz.getCanonicalName());
                    if (!isPost) {
                        restTemplate.getForObject("http://localhost/" + uri + "?exception={exception}", String.class, clazz.getCanonicalName());
                    } else {
                        restTemplate.postForObject("http://localhost/" + uri + "?exception={exception}", null, String.class, clazz.getCanonicalName());
                    }
                } catch (HttpClientErrorException | HttpServerErrorException e) {
                    try {
                        ApiError apiError = objectMapper.readValue(e.getResponseBodyAsString(), ApiError.class);
                        HttpStatus value = AnnotationUtils.findAnnotation(clazz, ResponseStatus.class).value();
                        Assertions.assertEquals(value.value(), e.getRawStatusCode());
                        Assertions.assertEquals(value, HttpStatus.valueOf(apiError.getStatus()));
                        Assertions.assertEquals(uri, apiError.getUri());
                        Assertions.assertEquals(clazz.getCanonicalName(), apiError.getError());
                    } catch (JsonProcessingException ex) {
                        throw ex;
                    }
                    continue;
                }
                Assertions.fail();
            }
        }
    }

    /**
     * 测试能够转换所有ApiError注解的异常并成功转为ApiError对象
     *
     * @throws JsonProcessingException - 解析错误
     */
    @Test
    public void testEndpointThrown() throws JsonProcessingException {
        doTest("/controller/exception", false);
    }

    /**
     * 检查Filter抛出的异常
     *
     * @throws JsonProcessingException - 解析错误
     */
    @Test
    public void testFilterThrown() throws JsonProcessingException {
        doTest("/filter/exception", false);
    }

    /**
     * 测试对{@link ResponseStatus}注解的异常能否被正常转为状态码(controller)
     *
     * @throws JsonProcessingException - 解析错误
     */
    @Test
    public void testResponseStatusExceptionFromController() throws JsonProcessingException {
        try {
            restTemplate.getForObject("http://localhost/controller/exception?exception={exception}", String.class, ResponseStatusAnnotatedException.class.getCanonicalName());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            ApiError apiError = objectMapper.readValue(e.getResponseBodyAsString(), ApiError.class);
            HttpStatus value = ResponseStatusAnnotatedException.class.getAnnotation(ResponseStatus.class).value();
            Assertions.assertEquals(value.value(), e.getRawStatusCode());
            Assertions.assertEquals(value, HttpStatus.valueOf(apiError.getStatus()));
            Assertions.assertEquals(ResponseStatusAnnotatedException.class.getCanonicalName(), apiError.getError());
            return;
        }
        Assertions.fail();
    }

    /**
     * 测试对{@link ResponseStatus}注解的异常能否被正常转为状态码(filter)
     *
     * @throws JsonProcessingException - 解析错误
     */
    @Test
    public void testResponseStatusExceptionFromFilter() throws JsonProcessingException {
        try {
            restTemplate.getForObject("http://localhost/filter/exception?exception={exception}", String.class, ResponseStatusAnnotatedException.class.getCanonicalName());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            ApiError apiError = objectMapper.readValue(e.getResponseBodyAsString(), ApiError.class);
            HttpStatus value = ResponseStatusAnnotatedException.class.getAnnotation(ResponseStatus.class).value();
            Assertions.assertEquals(value.value(), e.getRawStatusCode());
            Assertions.assertEquals(value, HttpStatus.valueOf(apiError.getStatus()));
            Assertions.assertEquals(ResponseStatusAnnotatedException.class.getCanonicalName(), apiError.getError());
            return;
        }
        Assertions.fail();
    }

    @Test
    public void testIgnore() throws JsonProcessingException {
        try {
            restTemplate.getForObject("http://localhost/controller/ignore", String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            Map<String, Object> response = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<Map<String, Object>>() {
            });
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            //这里正常error是bad request的类路径
            Assertions.assertEquals("Bad Request", response.get("error"));
            return;
        }
        Assertions.fail();
    }
}
