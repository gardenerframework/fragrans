package com.jdcloud.gardener.fragrans.validation.constraints.map;

import com.jdcloud.gardener.fragrans.validation.constraints.AbstractConstraintValidator;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 实现{@link OneToOneMapping}注解的验证
 *
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.0.0
 */
public class OneToOneMappingValidator extends AbstractConstraintValidator<OneToOneMapping, Map<?, ?>> {
    @Override
    protected boolean validate(Map<?, ?> value, ConstraintValidatorContext context, Map<String, Object> data) {
        if (CollectionUtils.isEmpty(value)) {
            return true;
        }
        //查看value中是否有重复数据
        Set<?> keySet = value.keySet();
        //如果是1对1映射，这两个值转成set后要能对上
        Set<?> valueSet = new HashSet<>(value.values());
        //null视作没有值
        return !valueSet.contains(null) && keySet.size() == valueSet.size();
    }
}
