package io.gardenerframework.fragrans.sugar.lang.type.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhanghan30
 * @date 2022/9/16 3:59 下午
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface Uninherit {
    /**
     * 不继承的类清单
     *
     * @return 清单
     */
    Class<?>[] value();
}
