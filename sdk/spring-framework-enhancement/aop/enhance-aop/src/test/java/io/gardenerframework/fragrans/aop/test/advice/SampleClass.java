package io.gardenerframework.fragrans.aop.test.advice;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SampleClass {
    public String method() {
        return UUID.randomUUID().toString();
    }
}
