package com.jdcloud.gardener.fragrans.validation.constraints.collection;

import com.jdcloud.gardener.fragrans.validation.constraints.AbstractConstraintValidator;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 实现{@link DistinctItem}注解的验证
 *
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.0.0
 */
public class DistinctItemValidator extends AbstractConstraintValidator<DistinctItem, Collection<?>> {

    @Override
    protected boolean validate(Collection<?> value, ConstraintValidatorContext context, Map<String, Object> data) {
        if (CollectionUtils.isEmpty(value)) {
            return true;
        }
        Set<Object> itemSet = new HashSet<>();
        for (Object item : value) {
            if (itemSet.contains(item)) {
                return false;
            }
            itemSet.add(item);
        }
        return true;
    }
}
