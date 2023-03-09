package io.gardenerframework.fragrans.api.test.cases;

import io.gardenerframework.fragrans.api.test.ApiAdviceTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author ZhangHan
 * @date 2022/5/14 2:07
 */
@SpringBootTest(classes = ApiAdviceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndpointHandlerMethodAdviceTest {
    @LocalServerPort
    public int port;

    @Test
    @DisplayName("advice冒烟测试")
    void smokeTest() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getForObject("http://localhost:{port}/EndpointHandlerMethodAdvice", void.class, port);
        } catch (HttpClientErrorException exception) {
            Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
            return;
        }
        Assertions.fail();
    }

    @Test
    @DisplayName("adapter冒烟测试")
    public void adapterTest() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getForObject("http://localhost:{port}/EndpointHandlerMethodBeforeAdviceAdapterEndpoint", void.class, port);
        } catch (HttpServerErrorException exception) {
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
            return;
        }
        Assertions.fail();
    }

    @Test
    @DisplayName("adapter对于未实现方法的冒烟测试")
    public void adapterNonTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://localhost:{port}/EndpointHandlerMethodBeforeAdviceAdapterEndpoint", null, void.class, port);
    }

    @Test
    @DisplayName("adapter对于未关注类型的方法的冒烟测试")
    public void adapterWithTypeNonTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://localhost:{port}/EndpointHandlerMethodBeforeAdviceAdapterEndpoint", null, void.class, port);
    }
}
