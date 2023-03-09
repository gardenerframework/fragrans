package com.jdcloud.gardener.fragrans.api.idempotent.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhanghan30
 * @date 2022/2/24 3:57 下午
 */
@SpringBootApplication
public class IdempotentApiTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdempotentApiTestApplication.class, args);
    }
}
