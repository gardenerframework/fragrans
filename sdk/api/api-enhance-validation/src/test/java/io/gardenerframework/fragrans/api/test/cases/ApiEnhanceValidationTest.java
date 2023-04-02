package io.gardenerframework.fragrans.api.test.cases;

import io.gardenerframework.fragrans.api.test.ApiAdviceTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/5/14 2:07
 */
@SpringBootTest(classes = ApiAdviceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiEnhanceValidationTest {
    @LocalServerPort
    public int port;

    @Test
    @DisplayName("advice冒烟测试")
    void smokeTest() {
        RestTemplate restTemplate = new RestTemplate();
        HttpClientErrorException exception = null;
        try {
            restTemplate.getForObject("http://localhost:{port}/validate/-5", void.class, port);
        } catch (HttpClientErrorException e) {
            exception = e;
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        exception = null;
        try {
            HashMap<Object, Map> param = new HashMap<>();
            param.put("nested", new HashMap<>());
            param.get("nested").put("date", "2022-01-01T00:00:00");
            restTemplate.postForObject("http://localhost:{port}/validate/json",
                    param,
                    void.class,
                    port
            );
        } catch (HttpClientErrorException e) {
            exception = e;
        }
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
