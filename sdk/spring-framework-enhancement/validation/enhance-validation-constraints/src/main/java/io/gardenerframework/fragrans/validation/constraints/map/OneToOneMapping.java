package io.gardenerframework.fragrans.validation.constraints.map;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 对{@code List}类型的属性进行校验，要求内部的容器数据是一对一映射
 * <p>
 * 比如List 内是 学号 和 身份证，那么已知一个身份证和一个学号一对一映射
 *
 * @author zhanghan
 * @date 2020-11-07 12:45
 * @since 1.0.0
 */
@Documented
@Constraint(validatedBy = OneToOneMappingValidator.class)
@Target({FIELD, PARAMETER, TYPE_USE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface OneToOneMapping {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
