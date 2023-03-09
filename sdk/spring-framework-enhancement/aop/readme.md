# 已有AOP注解的问题

spring提供了诸如"@Around"
的aop注解来指明需要切入的方法、类型以及注解，但是这种注解的参数往往是一个字符串而不是类型。这就导致了如果切点的名称，路径，返回值，参数等发生了变动，拦截器是无法从编译上获得任何感知。最终出现生产事故后查了半天可能发现原因是多了个参数或者改了个名字导致切面并没有进入

# 前置切面改造

面对这种问题，开发更多的是希望

* 声明一个bean，继承要拦截的类，或者实现了要拦截的类相同的接口
* 将要拦截的方法进行"Override"，在执行目标方法前执行拦截器的同样名称，同样参数结构的方法
*

如果拦截器本身继承自另一个拦截器，则查看当前拦截器找到的方法的声明类是否是一个[EnhanceMethodInterceptor](enhance-aop%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Faop%2Finterceptor%2FEnhanceMethodInterceptor.java)

# EnhanceMethodInterceptor

```java
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
```

这是组件的主要接口，它在前置切面中声明了查找同名和同参数类型方法的逻辑，并且要求方法的声明类必须是`EnhanceMethodInterceptor`
，否则不调用。同时，`PointcutAdvisor`要求方法拦截器给出关注的`Pointcut`

# Pointcut

组件内置了[CriteriaPointcut](enhance-aop%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Faop%2Fpointcut%2FCriteriaPointcut.java)
，要求给出基于类和方法的过滤条件，有关使用条件进行过滤的模式，参考[criteria](enhance-aop%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Faop%2Fcriteria)
的介绍

* [ClassHasAnnotationCriteria](enhance-aop%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Faop%2Fcriteria%2FClassHasAnnotationCriteria.java):
  要求被拦截的类必须具有注解
* [ClassMatchesCriteria](enhance-aop%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Faop%2Fcriteria%2FClassMatchesCriteria.java):
  要求被拦截的类必须是XX类型(而不是子类或者父类)
* [IsSubclassCriteria](enhance-aop%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Faop%2Fcriteria%2FIsSubclassCriteria.java):
  要求被拦截的类是某个接口的实现或者某个类的子类
* [MethodHasAnnotationCriteria](enhance-aop%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Faop%2Fcriteria%2FMethodHasAnnotationCriteria.java):
  要求被拦截的方法具有注解