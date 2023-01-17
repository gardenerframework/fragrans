package com.jdcloud.gardener.fragrans.api.options.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 核心注解，用于指示当前bean是一个api选项
 *
 * @author zhanghan30
 * @date 2022/1/3 8:18 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApiOption {
    /**
     * 是否只读
     *
     * @return 是否是只读选项，意思是不能动态去修改
     */
    boolean readonly();
}
