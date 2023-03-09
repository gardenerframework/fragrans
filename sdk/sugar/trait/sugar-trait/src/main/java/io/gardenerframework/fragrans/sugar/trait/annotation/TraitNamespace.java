package io.gardenerframework.fragrans.sugar.trait.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当前类是一个命名空间，用来存放带有{@link Trait}注解的类
 *
 * @author zhanghan30
 * @date 2022/8/14 1:58 上午
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface TraitNamespace {
}
