package io.gardenerframework.fragrans.validation.constraints.collection;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 对{@code List}类型的属性进行校验，要求列表内不能有重复出现的值
 *
 * @author zhanghan
 * @date 2020-11-07 12:45
 * @since 1.0.0
 */
@Documented
@Constraint(validatedBy = DistinctItemValidator.class)
@Target({FIELD, PARAMETER, TYPE_USE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface DistinctItem {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
