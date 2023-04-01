package io.gardenerframework.fragrans.api.validation;


import io.gardenerframework.fragrans.api.standard.error.exception.client.BadRequestArgumentException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 用来验证那种map格式的数据
 */
@RequiredArgsConstructor
public class HandlerMethodArgumentBeanValidator {
    @NonNull
    private final Validator validator;

    /**
     * 完成验证
     *
     * @param object 要验证的对象
     * @return 是否包含验证失败
     */
    @Nullable
    private <T> Map<String, Object> doValidate(T object) {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        Map<String, Object> details;
        if (!CollectionUtils.isEmpty(violations)) {
            details = new HashMap<>(violations.size());
            violations.forEach(
                    constraintViolation -> {
                        Path propertyPath = constraintViolation.getPropertyPath();
                        details.put(propertyPath.toString(), constraintViolation.getMessage());
                    }
            );
            return details;
        }
        return null;
    }

    /**
     * 验证，失败了就抛异常
     *
     * @param target 要求验证的东西
     * @throws BadRequestArgumentException 验证失败
     */
    public <T> void validate(@NotNull T target) throws BadRequestArgumentException {
        Map<String, Object> violations = doValidate(target);
        if (violations != null) {
            throw new BadRequestArgumentException(violations);
        }
    }
}
