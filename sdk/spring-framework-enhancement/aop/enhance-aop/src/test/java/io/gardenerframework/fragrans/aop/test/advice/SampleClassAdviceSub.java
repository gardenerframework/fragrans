package io.gardenerframework.fragrans.aop.test.advice;

import io.gardenerframework.fragrans.aop.criteria.ClassMatchesCriteria;
import io.gardenerframework.fragrans.aop.interceptor.EnhanceMethodInterceptor;
import io.gardenerframework.fragrans.aop.pointcut.CriteriaPointcut;
import org.springframework.aop.Pointcut;
import org.springframework.stereotype.Component;

@Component
public class SampleClassAdviceSub extends SampleClassAdvice implements EnhanceMethodInterceptor {
}
