package io.gardenerframework.fragrans.data.schema.annotation;

import java.lang.annotation.*;

/**
 * 代表列是由数据库控制的
 *
 * @author zhanghan
 * @date 2021/4/15 01:08
 * @since 1.0.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseControlledField {
}
