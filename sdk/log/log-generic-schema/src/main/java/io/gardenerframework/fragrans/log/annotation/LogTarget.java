package io.gardenerframework.fragrans.log.annotation;

import java.lang.annotation.*;

/**
 * 用于记录日志的类
 *
 * @author zhanghan30
 * @date 2021/11/9 11:53 上午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogTarget {
    String value();
}
