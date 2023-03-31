package io.gardenerframework.fragrans.api.validation;


import com.fasterxml.jackson.databind.ObjectMapper;
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
@SuppressWarnings("rawtypes")
public class JsonParameterValidator {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator;

    /**
     * 完成验证
     *
     * @param json      json
     * @param targetTpe 目标类型
     * @return 是否包含验证失败
     */
    @Nullable
    private <T> Map<String, Object> doValidate(@NonNull Map json, @NotNull Class<T> targetTpe) {
        T t = objectMapper.convertValue(json, targetTpe);
        Set<ConstraintViolation<Object>> violations = validator.validate(t);
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
     * 尝试验证，不抛异常
     *
     * @param json      json
     * @param targetTpe 目标类型
     * @return 验证是否成功
     */
    public <T> boolean tryValidate(@NonNull Map json, @NotNull Class<T> targetTpe) {
        return doValidate(json, targetTpe) == null;
    }

    /**
     * 验证，失败了就抛异常
     *
     * @param json      json
     * @param targetTpe 目标类型
     * @throws BadRequestArgumentException 验证失败
     */
    public <T> void validate(@NonNull Map json, @NotNull Class<T> targetTpe) throws BadRequestArgumentException {
        Map<String, Object> violations = doValidate(json, targetTpe);
        if (violations != null) {
            throw new BadRequestArgumentException(violations);
        }
    }
}
