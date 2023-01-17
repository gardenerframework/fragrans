package io.gardenerframework.fragrans.sugar.lang.method.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhanghan30
 * @date 2022/9/14 4:52 下午
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RewriteReturnValueType {
    /**
     * 要将返回值改成什么类型
     *
     * @return 类型
     */
    Class<?> value();
}
