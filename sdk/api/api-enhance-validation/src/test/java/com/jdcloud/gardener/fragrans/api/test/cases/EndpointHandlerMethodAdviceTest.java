package com.jdcloud.gardener.fragrans.api.test.cases;

import com.jdcloud.gardener.fragrans.api.test.ApiAdviceTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
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
            restTemplate.getForObject("http://localhost:{port}/validate/-5", void.class, port);
        } catch (HttpClientErrorException exception) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            return;
        }
        Assertions.fail();
    }
}
