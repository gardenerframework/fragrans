package com.jdcloud.gardener.fragrans.api.advice.engine;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zhanghan
 * @date 2021/9/15 14:38
 */

public abstract class EndpointHandlerMethodBeforeAdviceAdapter implements EndpointHandlerMethodAdvice {
    /**
     * 要求匹配的接口类型
     * <p>
     * 当指定类型时，只有当前接口时其子类时才会生效
     */
    @Nullable
    private final Class<?> matchType;

    protected EndpointHandlerMethodBeforeAdviceAdapter(Class<?> matchType) {
        this.matchType = matchType;
    }

    protected EndpointHandlerMethodBeforeAdviceAdapter() {
        this.matchType = null;
    }

    /**
     * 覆盖和重写before方法，检查实现类是否有和被代理的对象一模一样的方法，有的话直接invoke
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @throws Exception 执行过程中产生的异常
     */
    @Override
    public final void before(Object target, MethodSignature methodSignature, Object[] arguments) throws Exception {
        if (this.matchType != null && target != null && !(this.matchType.isAssignableFrom(ClassUtils.getUserClass(target.getClass())))) {
            //不是关注的类型
            return;
        }
        try {
            Method method = this.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
            method.invoke(this, arguments);
        } catch (NoSuchMethodException | SecurityException e) {
            //没有这个方法
        } catch (InvocationTargetException e) {
            Throwable wrapped = e.getCause();
            if (wrapped instanceof Exception) {
                throw (Exception) wrapped;
            } else {
                throw e;
            }
        }
    }
}
