package io.gardenerframework.fragrans.aop.test.advice;

import io.gardenerframework.fragrans.aop.interceptor.EnhanceMethodInterceptor;
import org.springframework.stereotype.Component;

@Component
public class SampleClassAdviceSub extends SampleClassAdvice implements EnhanceMethodInterceptor {
}
