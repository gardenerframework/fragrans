package com.jdcloud.gardener.fragrans.api.advice.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * @author ZhangHan
 * @date 2021/8/21 5:07
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
@Component
public class EndpointHandlerMethodInterceptor {
    private final List<EndpointHandlerMethodAdvice> advices;

    private void doFailAdvice(Object target, MethodSignature methodSignature, Object[] arguments, Throwable throwable) throws Exception {
        if (throwable instanceof Exception) {
            for (EndpointHandlerMethodAdvice advice : advices) {
                advice.fail(target, methodSignature, arguments, (Exception) throwable);
            }
        }
    }

    private void doBeforeAdvice(Object target, MethodSignature methodSignature, Object[] arguments) throws Exception {
        for (EndpointHandlerMethodAdvice advice : advices) {
            advice.before(target, methodSignature, arguments);
        }
    }

    private void doAfterAdvice(Object target, MethodSignature methodSignature, Object[] arguments, Object returnValue) throws Exception {
        for (EndpointHandlerMethodAdvice advice : advices) {
            advice.after(target, methodSignature, arguments, returnValue);
        }
    }

    @Around("(@annotation(org.springframework.web.bind.annotation.RequestMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public Object interceptEndpointHandlerMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //切入的类压根不是个controller
        //比如是个feign接口
        //@RestController也是controller
        if (AnnotationUtils.findAnnotation(ClassUtils.getUserClass(proceedingJoinPoint.getTarget()), Controller.class) == null) {
            return proceedingJoinPoint.proceed();
        }
        /**
         * 如果切入的不是一个endpoint的方法，则直接执行
         */
        if (!(proceedingJoinPoint.getSignature() instanceof MethodSignature)) {
            return proceedingJoinPoint.proceed();
        } else {
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            Object target = proceedingJoinPoint.getTarget();
            Object[] arguments = proceedingJoinPoint.getArgs();
            doBeforeAdvice(target, signature, arguments);
            try {
                Object returnValue = proceedingJoinPoint.proceed();
                doAfterAdvice(target, signature, arguments, returnValue);
                return returnValue;
            } catch (Throwable throwable) {
                doFailAdvice(target, signature, arguments, throwable);
                throw throwable;
            }
        }
    }
}
