package io.gardenerframework.fragrans.data.persistence.criteria.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chris
 * <p>
 * date: 2023/11/4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TypeConstraints {
    Class<?> value();
}
