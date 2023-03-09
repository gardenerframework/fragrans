package io.gardenerframework.fragrans.api.test;

import io.gardenerframework.fragrans.api.standard.error.configuration.RevealError;
import io.gardenerframework.fragrans.api.test.cases.DefaultApiErrorFactoryTest;
import io.gardenerframework.fragrans.api.test.cases.exception.ResponseStatusAnnotatedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RevealError(superClasses = {DefaultApiErrorFactoryTest.SuperClass.class, ResponseStatusAnnotatedException.class})
public class ApiStandardErrorTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiStandardErrorTestApplication.class, args);
    }
}