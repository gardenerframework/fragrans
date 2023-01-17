package com.jdcloud.gardener.fragrans.api.test;

import com.jdcloud.gardener.fragrans.api.standard.error.configuration.RevealError;
import com.jdcloud.gardener.fragrans.api.test.cases.DefaultApiErrorFactoryTest;
import com.jdcloud.gardener.fragrans.api.test.cases.exception.ResponseStatusAnnotatedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RevealError(superClasses = {DefaultApiErrorFactoryTest.SuperClass.class, ResponseStatusAnnotatedException.class})
public class ApiStandardErrorTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiStandardErrorTestApplication.class, args);
    }
}