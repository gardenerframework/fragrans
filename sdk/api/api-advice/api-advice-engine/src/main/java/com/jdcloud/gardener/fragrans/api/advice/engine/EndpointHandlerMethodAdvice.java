package com.jdcloud.gardener.fragrans.api.advice.engine;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan
 * @date 2021/8/25 15:09
 */
public interface EndpointHandlerMethodAdvice {
    /**
     * 在controller的端点方法执行前
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void before(Object target, MethodSignature methodSignature, Object[] arguments) throws Exception {
    }

    /**
     * 在controller的端点方法执行后
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @param returnValue     返回值
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void after(Object target, MethodSignature methodSignature, Object[] arguments, @Nullable Object returnValue) throws Exception {
    }

    /**
     * 在捕捉到异常时
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @param exception       捕捉到了什么异常
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void fail(Object target, MethodSignature methodSignature, Object[] arguments, Exception exception) throws Exception {

    }
}
