package com.jdcloud.gardener.fragrans.validation.constraints.text;

import com.jdcloud.gardener.fragrans.validation.constraints.AbstractConstraintValidator;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 实现{@link UpperUnderscore}注解的验证
 *
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.1.0
 */
public class UpperUnderscoreValidator extends AbstractConstraintValidator<UpperUnderscore, String> {
    private final Pattern pattern = Pattern.compile("^[A-Z][A-Z0-9_]*[A-Z0-9]|^[A-Z]$");

    @Override
    protected boolean validate(String value, ConstraintValidatorContext context, Map<String, Object> data) {
        if (value == null) {
            return true;
        }
        return pattern.matcher(value).matches();
    }
}
