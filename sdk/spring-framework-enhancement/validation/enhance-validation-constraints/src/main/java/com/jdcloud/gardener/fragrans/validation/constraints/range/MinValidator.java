package com.jdcloud.gardener.fragrans.validation.constraints.range;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现{@link Max}注解的验证
 *
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.1.0
 */
@Slf4j
public class MinValidator extends AbstractRangeConstraintValidator<Min> implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private MinConstraintProvider provider;

    @Override
    public void initialize(Min constraintAnnotation) {
        provider = this.applicationContext.getBean(constraintAnnotation.provider());
    }

    @Override
    protected Map<String, Object> getMessageParameters(Number value, Map<String, Object> data) {
        return new HashMap<String, Object>(1) {{
            put("min", data.get(MinValidator.class.getName()));
        }};
    }

    @Override
    protected boolean validate(Number value, ConstraintValidatorContext context, Map<String, Object> data) {
        if (value == null) {
            return true;
        }
        Number min = provider.getMin();
        checkTypeCompatibility(value, min);
        data.put(MinValidator.class.getName(), min);
        if (isInteger(value)) {
            return value.longValue() >= min.longValue();
        } else {
            return value.doubleValue() >= min.doubleValue();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
