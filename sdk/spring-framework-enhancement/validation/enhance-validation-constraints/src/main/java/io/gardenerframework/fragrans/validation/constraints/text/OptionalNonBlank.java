package io.gardenerframework.fragrans.validation.constraints.text;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 对{@code String}类型的属性进行校验，要求该属性:
 * <uL>
 * <li>或者是{@code null}</li>
 * <li>或者是一个具备文字的字符串</li>
 * </uL>
 * 这是因为在大部分场景下，一些可选参数或者传{@code null}或者得写点空格之外的东西，比如说住址，邮编
 *
 * @author zhanghan
 * @date 2020-11-07 12:45
 * @since 1.0.0
 */
@Documented
@Constraint(validatedBy = OptionalNonBlankValidator.class)
@Target({FIELD, PARAMETER, TYPE_USE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface OptionalNonBlank {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
