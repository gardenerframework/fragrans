package io.gardenerframework.fragrans.data.persistence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解在mapper类上，方便不声明父接口的方法就全局替换接口上的sql provider注解内容
 *
 * @author zhanghan30
 * @date 2022/9/23 00:42
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OverrideSqlProviderAnnotation {
    /**
     * 覆盖value字段
     *
     * @return 覆盖后的value
     */
    Class<?> value();
}
