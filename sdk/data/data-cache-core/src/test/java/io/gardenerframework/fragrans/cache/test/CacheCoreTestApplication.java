package io.gardenerframework.fragrans.cache.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zhanghan30
 * @date 2022/2/14 1:31 下午
 */
@SpringBootApplication
@EnableScheduling
public class CacheCoreTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(CacheCoreTestApplication.class, args);
    }
}
