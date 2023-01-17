package com.jdcloud.gardener.fragrans.event;

import java.lang.annotation.*;

/**
 * @author zhanghan30
 * @date 2021/10/27 6:56 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Topic {
    String value();
}
