package io.gardenerframework.fragrans.validation.constraints.text;

import io.gardenerframework.fragrans.validation.constraints.AbstractConstraintValidator;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * 实现{@link OptionalNonBlank}注解的验证
 *
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.0.0
 */
public class OptionalNonBlankValidator extends AbstractConstraintValidator<OptionalNonBlank, String> {
    @Override
    protected boolean validate(String value, ConstraintValidatorContext context, Map<String, Object> data) {
        return value == null || StringUtils.hasText(value);
    }
}
