package io.gardenerframework.fragrans.aop.interceptor;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface EnhanceMethodInterceptor extends PointcutAdvisor, MethodInterceptor {
    /**
     * 在controller的端点方法执行前
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void before(Object target, Method methodSignature, Object[] arguments) throws Exception {
        try {
            //寻找同样声明的方法(参数一样，名称一样)
            Method method = this.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
            if (EnhanceMethodInterceptor.class.isAssignableFrom(method.getDeclaringClass())) {
                //方法来自切面类的声明
                method.invoke(this, arguments);
            }
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

    /**
     * 在controller的端点方法执行后
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @param returnValue     返回值
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void after(Object target, Method methodSignature, Object[] arguments, @Nullable Object returnValue) throws Exception {
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
    default void fail(Object target, Method methodSignature, Object[] arguments, Exception exception) throws Exception {

    }

    /**
     * 返回切面
     *
     * @return 当前对象就是切面
     */
    @Override
    default Advice getAdvice() {
        return this;
    }

    /**
     * 是否每个实例一个代理
     *
     * @return 是
     */
    @Override
    default boolean isPerInstance() {
        return true;
    }

    /**
     * 方法调用
     *
     * @param invocation the method invocation joinpoint
     * @return 调用结果
     * @throws Throwable 异常
     */
    @Override
    default Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object target = invocation.getThis();
        Object[] arguments = invocation.getArguments();
        before(target, method, arguments);
        try {
            Object returnValue = invocation.proceed();
            after(target, method, arguments, returnValue);
            return returnValue;
        } catch (Throwable throwable) {
            if (throwable instanceof Exception) {
                fail(target, method, arguments, (Exception) throwable);
            }
            throw throwable;
        }
    }
}
