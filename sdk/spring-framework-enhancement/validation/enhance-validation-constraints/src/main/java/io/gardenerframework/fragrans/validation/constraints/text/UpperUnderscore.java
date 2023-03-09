package io.gardenerframework.fragrans.validation.constraints.text;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 检查是否是一个全大写的下划线风格的字符串
 *
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.0.0
 */
@Documented
@Constraint(validatedBy = UpperUnderscoreValidator.class)
@Target({FIELD, PARAMETER, TYPE_USE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface UpperUnderscore {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
