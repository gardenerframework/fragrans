package io.gardenerframework.fragrans.api.test.endpoints;

import io.gardenerframework.fragrans.aop.criteria.ClassMatchesCriteria;
import io.gardenerframework.fragrans.aop.pointcut.CriteriaPointcut;
import io.gardenerframework.fragrans.api.advice.engine.EndpointHandlerMethodAdvice;
import io.gardenerframework.fragrans.pattern.criteria.schema.object.BooleanCriteria;
import io.gardenerframework.fragrans.pattern.criteria.schema.root.BaseBooleanCriteria;
import org.springframework.aop.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author zhanghan30
 * @date 2022/7/7 5:56 下午
 */
@Component
public class EndpointHandlerMethodBeforeAdviceAdapterEndpointAdvice implements EndpointHandlerMethodAdvice {
    public void test() {
        throw new RuntimeException();
    }

    @Override
    public Pointcut getPointcut() {
        Pointcut pointcut = EndpointHandlerMethodAdvice.super.getPointcut();
        Assert.isInstanceOf(CriteriaPointcut.class, pointcut);
        CriteriaPointcut criteriaPointcut = (CriteriaPointcut) pointcut;
        criteriaPointcut.setClassCriteria(
                BooleanCriteria.<Class<?>>builder()
                        .a(criteriaPointcut.getClassCriteria())
                        .operator(BaseBooleanCriteria.Operator.AND)
                        .b(ClassMatchesCriteria.builder().target(EndpointHandlerMethodBeforeAdviceAdapterEndpoint.class).build())
                        .build()
        );
        pointcut.getClassFilter();
        return pointcut;
    }
}
