package io.gardenerframework.fragrans.api.security.automation.data.test.cases;

import io.gardenerframework.fragrans.api.security.automation.data.test.OperatorInjectionApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhanghan30
 * @date 2023/2/3 13:21
 */
@SpringBootTest(classes = OperatorInjectionApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OperatorInjectorTest {
    @LocalServerPort
    private int port;

    @Test
    public void smokeTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject("http://localhost:{port}", void.class, port);
    }
}
