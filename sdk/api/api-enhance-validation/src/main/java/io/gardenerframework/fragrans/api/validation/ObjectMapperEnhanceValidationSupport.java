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
public class ObjectMapperEnhanceValidationSupport {
    private final ObjectMapper objectMapper = new ObjectMapper();
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
     * 尝试验证，不抛异常
     *
     * @param source    源
     * @param targetTpe 目标类型
     * @return 验证是否成功
     */
    @Nullable
    public <S, T> T tryConvert(@NonNull S source, @NotNull Class<T> targetTpe) {
        T t = objectMapper.convertValue(source, targetTpe);
        Map<String, Object> violations = doValidate(t);
        if (violations != null) {
            return null;
        }
        return t;
    }

    /**
     * 验证，失败了就抛异常
     *
     * @param source    源
     * @param targetTpe 目标类型
     * @throws BadRequestArgumentException 验证失败
     */
    public <S, T> T convert(@NonNull S source, @NotNull Class<T> targetTpe) throws BadRequestArgumentException {
        T t = objectMapper.convertValue(source, targetTpe);
        Map<String, Object> violations = doValidate(t);
        if (violations != null) {
            throw new BadRequestArgumentException(violations);
        }
        return t;
    }
}
