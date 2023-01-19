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
 * @date 2022/11/16 15:19
 */
@Component
public class EndpointHandlerMethodBeforeAdviceAdapterEndpointAdviceWithMatchType implements EndpointHandlerMethodAdvice {
    public Pointcut getPointcut() {
        Pointcut pointcut = EndpointHandlerMethodAdvice.super.getPointcut();
        Assert.isInstanceOf(CriteriaPointcut.class, pointcut);
        CriteriaPointcut criteriaPointcut = (CriteriaPointcut) pointcut;
        criteriaPointcut.setClassCriteria(
                BooleanCriteria.<Class<?>>builder()
                        .a(criteriaPointcut.getClassCriteria())
                        .operator(BaseBooleanCriteria.Operator.AND)
                        .b(ClassMatchesCriteria.builder().target(EndpointHandlerMethodBeforeAdviceAdapterEndpointAdviceWithMatchType.class).build())
                        .build()
        );
        pointcut.getClassFilter();
        return pointcut;
    }

    public void nonTest() {
        throw new RuntimeException();
    }
}
