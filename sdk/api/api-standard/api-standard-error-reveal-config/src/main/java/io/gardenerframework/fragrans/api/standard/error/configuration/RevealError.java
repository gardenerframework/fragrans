package io.gardenerframework.fragrans.api.standard.error.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 核心注解
 * <p>
 * 注解在{@link org.springframework.context.annotation.Configuration}类上时，则使用包基准类或基类来填写注册表
 * <p>
 * 注解在{@link Exception}的类上时，则表示当前类(包含其子类)是个要暴露的异常
 * <p>
 * 优先级是先找错误类上是不是有注解，没有才查看是否符合包和子类的范畴
 *
 * @author zhanghan30
 * @date 2022/8/26 8:03 上午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RevealError {
    /**
     * @return 要暴露的错误所在的包
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * @return 要暴露的错误的基类
     */
    Class<?>[] superClasses() default {};
}
