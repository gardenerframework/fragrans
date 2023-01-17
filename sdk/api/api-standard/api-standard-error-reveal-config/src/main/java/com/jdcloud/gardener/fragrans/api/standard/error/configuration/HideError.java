package com.jdcloud.gardener.fragrans.api.standard.error.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 和{@link RevealError}对应的，这个表达应当隐藏错误
 * <p>
 * 注解在{@link org.springframework.context.annotation.Configuration}类上时，则使用包基准类或基类来填写注册表
 * <p>
 * 注解在{@link Exception}的类上时，则表示当前类(包含其子类)是个要隐藏的异常
 * <p>
 * 隐藏的优先级高于暴露，如果相关的错误会被注册表优先判断
 * <p>
 * 优先级是先找错误类上是不是有注解，没有才查看是否符合包和子类的范畴
 *
 * @author zhanghan30
 * @date 2022/8/26 8:17 上午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HideError {
    /**
     * @return 要隐藏的错误所在的包
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * @return 要隐藏的错误的基类
     */
    Class<?>[] superClasses() default {};
}
