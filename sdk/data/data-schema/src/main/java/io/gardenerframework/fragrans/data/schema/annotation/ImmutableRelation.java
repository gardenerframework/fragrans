package io.gardenerframework.fragrans.data.schema.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记注解，代表关系一旦确认是不能变更的
 *
 * @author zhanghan
 * @date 2021/8/26 12:26
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ImmutableField
public @interface ImmutableRelation {
}
