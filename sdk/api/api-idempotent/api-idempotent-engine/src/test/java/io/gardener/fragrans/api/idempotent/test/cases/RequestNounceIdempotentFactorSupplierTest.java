package com.jdcloud.gardener.fragrans.api.idempotent.test.cases;

import com.jdcloud.gardener.fragrans.api.idempotent.engine.factor.IdempotentFactorSupplier;
import com.jdcloud.gardener.fragrans.api.idempotent.support.RequestNounceIdempotentFactorSupplier;
import com.jdcloud.gardener.fragrans.api.idempotent.test.IdempotentApiTestApplication;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/2/25 1:51 下午
 */
@SpringBootTest(classes = IdempotentApiTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(RequestNounceIdempotentFactorSupplierTest.TestEndpoint.class)
public class RequestNounceIdempotentFactorSupplierTest {

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("x-request-nounce 冒烟测试")
    public void smokeTest() {
        RestTemplate restTemplate = new RestTemplate();
        String nounce = UUID.randomUUID().toString();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("X-REQUEST-NOUNCE", nounce);
            return execution.execute(request, body);
        });
        Assertions.assertEquals(nounce, restTemplate.getForObject("http://localhost:{port}/test/nounce", String.class, port));
    }

    /**
     * 测试用的接口
     */
    @RestController
    @AllArgsConstructor
    public static class TestEndpoint {
        private final IdempotentFactorSupplier supplier;

        @RequestMapping("/test/nounce")
        public String testNounce(HttpServletRequest request) {
            Assertions.assertTrue(supplier instanceof RequestNounceIdempotentFactorSupplier);
            return supplier.getIdempotentFactor(new ServletServerHttpRequest(request));
        }
    }
}
