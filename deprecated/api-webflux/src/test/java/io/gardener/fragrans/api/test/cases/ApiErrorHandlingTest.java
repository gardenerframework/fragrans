package com.jdcloud.gardener.fragrans.api.test.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.function.Predicate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApiErrorHandlingTest {
    private Logger logger = LoggerFactory.getLogger(ApiErrorHandlingTest.class);
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void doTest(String uri) throws JsonProcessingException {
        String[] packages = new String[]{"com.jdcloud.gardener.fragrans.api.exception.client", "com.jdcloud.gardener.fragrans.api.exception.server"};
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
                    restTemplate.getForObject("http://localhost/" + uri + "?exception={exception}", String.class, clazz.getCanonicalName());
                } catch (HttpClientErrorException | HttpServerErrorException e) {
                    com.jdcloud.gardener.fragrans.api.standard.schema.response.schema.ApiError apiError = objectMapper.readValue(e.getResponseBodyAsString(), com.jdcloud.gardener.fragrans.api.standard.schema.response.schema.ApiError.class);
                    HttpStatus value = AnnotationUtils.findAnnotation(clazz, ResponseStatus.class).value();
                    Assertions.assertEquals(value.value(), e.getRawStatusCode());
                    Assertions.assertEquals(value, HttpStatus.valueOf(apiError.getStatus()));
                    Assertions.assertEquals(uri, apiError.getUri());
                    Assertions.assertEquals(clazz.getCanonicalName(), apiError.getError());
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
        doTest("/controller/exception");
    }
}
