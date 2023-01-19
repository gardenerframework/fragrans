package io.gardenerframework.fragrans.aop.test.advice;

import io.gardenerframework.fragrans.aop.criteria.ClassMatchesCriteria;
import io.gardenerframework.fragrans.aop.interceptor.EnhanceMethodInterceptor;
import io.gardenerframework.fragrans.aop.pointcut.CriteriaPointcut;
import org.springframework.aop.Pointcut;

public class SampleClassAdvice extends SampleClass implements EnhanceMethodInterceptor {
    @Override
    public String method() {
        throw new IllegalStateException();
    }

    @Override
    public Pointcut getPointcut() {
        return CriteriaPointcut.builder().classCriteria(
                ClassMatchesCriteria.builder().target(SampleClass.class).build()
        ).build();
    }
}
