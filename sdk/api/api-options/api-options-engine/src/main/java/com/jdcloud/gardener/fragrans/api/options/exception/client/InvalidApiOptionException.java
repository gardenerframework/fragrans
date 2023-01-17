package com.jdcloud.gardener.fragrans.api.options.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiErrorDetailsSupplier;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.BadRequestArgumentException;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zhanghan30
 * @date 2022/5/10 4:34 上午
 */
public class InvalidApiOptionException extends BadRequestArgumentException implements ApiErrorDetailsSupplier {
    private final transient Collection<ConstraintViolation<Object>> violations;

    public InvalidApiOptionException(Set<ConstraintViolation<Object>> violations) {
        super(violations.toString());
        this.violations = violations;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> details = new HashMap<>(violations.size());
        violations.forEach(
                violation -> details.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return details;
    }
}
