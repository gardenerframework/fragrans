package com.jdcloud.gardener.fragrans.api.idempotent.test.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jdcloud.gardener.fragrans.api.idempotent.engine.annotation.IdempotentApi;
import com.jdcloud.gardener.fragrans.api.idempotent.test.IdempotentApiTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/2/25 3:25 下午
 */
@SpringBootTest(classes = IdempotentApiTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({IdempotentApiTest.EntireClassProtectionTestEndpoint.class, IdempotentApiTest.MethodProtectionTestEndpoint.class})
public class IdempotentApiTest {
    private final RestTemplate restTemplate;
    private final String factor = UUID.randomUUID().toString();
    @LocalServerPort
    private int port;

    public IdempotentApiTest() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add(
                (request, body, execution) -> {
                    request.getHeaders().add("x-request-nounce", factor);
                    return execution.execute(request, body);
                }
        );
    }

    @Test
    @DisplayName("全类注解冒烟测试")
    public void classSmokeTest() throws JsonProcessingException {
        restTemplate.getForObject("http://localhost:{port}/test/class", void.class, port);
        try {
            restTemplate.getForObject("http://localhost:{port}/test/class", void.class, port);
        } catch (HttpClientErrorException e) {
            //理应抛出异常报错
            return;
        }
        Assertions.fail("出现重复提交");
    }

    @Test
    @DisplayName("全类注解的http方法逃逸冒烟测试")
    public void methodExcludeSmokeTest() {
        restTemplate.delete("http://localhost:{port}/test/class", port);
        restTemplate.delete("http://localhost:{port}/test/class", port);
    }

    @Test
    @DisplayName("方法注解冒烟测试")
    public void methodSmokeTest() {
        restTemplate.getForObject("http://localhost:{port}/test/method-none", void.class, port);
        restTemplate.getForObject("http://localhost:{port}/test/method-none", void.class, port);
        restTemplate.getForObject("http://localhost:{port}/test/method", void.class, port);
        try {
            restTemplate.getForObject("http://localhost:{port}/test/method", void.class, port);
        } catch (HttpClientErrorException e) {
            //理应抛出异常报错
            return;
        }
        Assertions.fail("出现重复提交");
    }


    @IdempotentApi(excludeMethods = HttpMethod.DELETE)
    @RestController
    public static class EntireClassProtectionTestEndpoint {
        @RequestMapping(value = "/test/class", method = {RequestMethod.GET, RequestMethod.DELETE})
        public void testClass() {

        }
    }

    @RestController
    public static class MethodProtectionTestEndpoint {
        @IdempotentApi
        @RequestMapping(value = "/test/method")
        public void testMethod() {

        }

        @RequestMapping(value = "/test/method-none")
        public void testMethodNone() {

        }
    }
}
