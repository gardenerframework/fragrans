package com.jdcloud.gardener.fragrans.api.idempotent.test.cases;

import com.jdcloud.gardener.fragrans.api.idempotent.core.IdempotentFactorStore;
import com.jdcloud.gardener.fragrans.api.idempotent.test.IdempotentApiTestApplication;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/2/25 2:48 下午
 */
@SpringBootTest(classes = IdempotentApiTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(CachedIdempotentFactorStoreTest.TestEndpoint.class)
public class CachedIdempotentFactorStoreTest {
    @LocalServerPort
    private int port;

    @Test
    @DisplayName("防重因子冒烟测试")
    public void smokeTest() {
        String factor = UUID.randomUUID().toString();
        RestTemplate restTemplate = new RestTemplate();
        Assertions.assertEquals(Boolean.TRUE, restTemplate.getForObject("http://localhost:{port}/test/store?factor={factor}", Boolean.class, port, factor));
        Assertions.assertEquals(Boolean.FALSE, restTemplate.getForObject("http://localhost:{port}/test/store?factor={factor}", Boolean.class, port, factor));
    }

    /**
     * 测试用的接口
     */
    @RestController
    @AllArgsConstructor
    public static class TestEndpoint {
        private final IdempotentFactorStore store;

        @RequestMapping("/test/store")
        public boolean testStore(@RequestParam("factor") String factor) {
            return store.saveIfAbsent(HttpMethod.GET, "/test/store", factor, Duration.ofMinutes(2));
        }
    }
}
