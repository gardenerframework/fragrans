package io.gardenerframework.fragrans.validation.constraints.range;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 检查参数是否在指定的最大值内
 * <p>
 * 这个最大值可以是动态获取的
 * <p>
 *
 * @author zhanghan30
 */
@Documented
@Constraint(validatedBy = MaxValidator.class)
@Target({FIELD, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface Max {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends MaxConstraintProvider> provider();
}
