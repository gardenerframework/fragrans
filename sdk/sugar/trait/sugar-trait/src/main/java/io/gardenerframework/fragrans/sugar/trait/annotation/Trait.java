package io.gardenerframework.fragrans.sugar.trait.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhanghan30
 * @date 2022/8/14 1:58 上午
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Trait {
    /**
     * 当前trait应当包装在那个类内
     * <p>
     * 类需要带有{@link TraitNamespace}注解
     *
     * @return 类的名称
     */
    Class<?> namespace() default Trait.class;
}
