package io.gardenerframework.fragrans.data.persistence.orm.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhanghan30
 * @date 2022/9/25 05:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ColumnTypeHandler {
    Class<? extends ColumnTypeHandlerProvider>[] provider();
}
