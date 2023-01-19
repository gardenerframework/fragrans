package io.gardenerframework.fragrans.api.advice.engine;

import io.gardenerframework.fragrans.aop.criteria.ClassHasAnnotationCriteria;
import io.gardenerframework.fragrans.aop.criteria.MethodHasAnnotationCriteria;
import io.gardenerframework.fragrans.aop.interceptor.EnhanceMethodInterceptor;
import io.gardenerframework.fragrans.aop.pointcut.CriteriaPointcut;
import io.gardenerframework.fragrans.pattern.criteria.schema.object.MatchAnyCriteria;
import org.springframework.aop.Pointcut;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * @author zhanghan
 * @date 2021/8/25 15:09
 */
public interface EndpointHandlerMethodAdvice extends EnhanceMethodInterceptor {
    @Override
    default Pointcut getPointcut() {
        return CriteriaPointcut.builder()
                //类上必须是Controller
                .classCriteria(ClassHasAnnotationCriteria.builder().annotation(Controller.class).build())
                .methodCriteria(MatchAnyCriteria.<Method>builder()
                        //方法具有这些注解
                        .criteria(MethodHasAnnotationCriteria.builder().annotation(RequestMapping.class).build())
                        .criteria(MethodHasAnnotationCriteria.builder().annotation(GetMapping.class).build())
                        .criteria(MethodHasAnnotationCriteria.builder().annotation(PostMapping.class).build())
                        .criteria(MethodHasAnnotationCriteria.builder().annotation(PutMapping.class).build())
                        .criteria(MethodHasAnnotationCriteria.builder().annotation(DeleteMapping.class).build())
                        .build())
                .build();
    }
}
