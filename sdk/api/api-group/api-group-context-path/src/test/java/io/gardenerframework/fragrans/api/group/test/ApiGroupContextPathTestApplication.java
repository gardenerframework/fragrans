package io.gardenerframework.fragrans.api.group.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhanghan30
 * @date 2022/6/24 6:10 下午
 */
@SpringBootApplication
public class ApiGroupContextPathTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGroupContextPathTestApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
