package com.jdcloud.gardener.fragrans.api.test;

import com.jdcloud.gardener.fragrans.api.standard.error.configuration.DomainErrorPackage;
import com.jdcloud.gardener.fragrans.api.test.exception.InheritedFromExistedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
@DomainErrorPackage(baseClasses = InheritedFromExistedException.class)
public class ApiWebFluxTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiWebFluxTestApplication.class, args);
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(200);
        threadPoolTaskExecutor.setCorePoolSize(100);
        return threadPoolTaskExecutor;
    }
}