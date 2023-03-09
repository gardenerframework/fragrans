package io.gardenerframework.fragrans.validation.constraints.range;

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
public class RangeValidator extends AbstractRangeConstraintValidator<Range> implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private MaxConstraintProvider maxConstraintProvider;
    private MinConstraintProvider minConstraintProvider;

    @Override
    public void initialize(Range constraintAnnotation) {
        maxConstraintProvider = this.applicationContext.getBean(constraintAnnotation.max());
        minConstraintProvider = this.applicationContext.getBean(constraintAnnotation.min());
    }

    @Override
    protected Map<String, Object> getMessageParameters(Number value, Map<String, Object> data) {
        return new HashMap<String, Object>(1) {{
            put("max", data.get(RangeValidator.class.getName() + ".max"));
            put("min", data.get(RangeValidator.class.getName() + ".min"));
        }};
    }

    @Override
    protected boolean validate(Number value, ConstraintValidatorContext context, Map<String, Object> data) {
        if (value == null) {
            return true;
        }
        Number max = maxConstraintProvider.getMax();
        Number min = minConstraintProvider.getMin();
        checkTypeCompatibility(value, max);
        checkTypeCompatibility(value, min);
        data.put(RangeValidator.class.getName() + ".max", max);
        data.put(RangeValidator.class.getName() + ".min", min);
        if (isInteger(value)) {
            return value.longValue() <= max.longValue() && value.longValue() >= min.longValue();
        } else {
            return value.doubleValue() <= max.doubleValue() && value.doubleValue() >= min.doubleValue();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
