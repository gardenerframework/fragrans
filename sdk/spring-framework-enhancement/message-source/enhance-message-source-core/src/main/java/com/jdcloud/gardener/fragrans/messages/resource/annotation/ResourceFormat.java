package com.jdcloud.gardener.fragrans.messages.resource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明资源类型
 *
 * @author ZhangHan
 * @date 2022/6/10 0:00
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceFormat {
    String value();
}
