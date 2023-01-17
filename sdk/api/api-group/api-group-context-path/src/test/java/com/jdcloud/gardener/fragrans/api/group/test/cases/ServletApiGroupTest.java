package com.jdcloud.gardener.fragrans.api.group.test.cases;

import com.jdcloud.gardener.fragrans.api.group.test.ApiGroupContextPathTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * @author ZhangHan
 * @date 2022/5/10 21:37
 */
@SpringBootTest(classes = ApiGroupContextPathTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServletApiGroupTest {
    @Autowired
    private RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void onControllerClassAndMethodTest() {
        String id = UUID.randomUUID().toString();
        restTemplate.getForObject("http://localhost:{port}/context-path/type/method", void.class, port);
        Assertions.assertEquals(id, restTemplate.getForObject("http://localhost:{port}/context-path/type/method/{id}", String.class, port, id));
        restTemplate.getForObject("http://localhost:{port}/context-path/method", void.class, port);
        Assertions.assertEquals(id, restTemplate.getForObject("http://localhost:{port}/context-path/method/{id}", String.class, port, id));


        restTemplate.getForObject("http://localhost:{port}/context-path2/type2/method2", void.class, port);
        Assertions.assertEquals(id, restTemplate.getForObject("http://localhost:{port}/context-path2/type2/method2/{id}", String.class, port, id));
        restTemplate.getForObject("http://localhost:{port}/context-path/test-same", String.class, port);
        restTemplate.getForObject("http://localhost:{port}/context-path2/test-same", String.class, port);
    }
}
