package io.gardenerframework.fragrans.api.validation;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.fragrans.api.standard.error.exception.client.BadRequestArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 用来验证那种map格式的数据
 */
@RequiredArgsConstructor
public class JsonParameterValidator {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator;

    @SuppressWarnings("rawtypes")
    public <T> void validate(Map json, Class<T> targetTpe) {
        T t = objectMapper.convertValue(json, targetTpe);
        Set<ConstraintViolation<Object>> violations = validator.validate(t);
        if (!CollectionUtils.isEmpty(violations)) {
            Map<String, Object> details = new HashMap<>(violations.size());
            violations.forEach(
                    constraintViolation -> {
                        Path propertyPath = constraintViolation.getPropertyPath();
                        details.put(propertyPath.toString(), constraintViolation.getMessage());
                    }
            );
            throw new BadRequestArgumentException(details);
        }

    }
}
