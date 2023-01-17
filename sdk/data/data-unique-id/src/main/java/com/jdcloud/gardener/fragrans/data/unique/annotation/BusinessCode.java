package com.jdcloud.gardener.fragrans.data.unique.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在类上，用于标记业务码
 *
 * @author zhanghan
 * @date 2021/9/27 16:45
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessCode {
    char value();
}
