package com.jdcloud.gardener.fragrans.data.persistence.criteria.support;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author ZhangHan
 * @date 2022/11/28 17:34
 */
@Component
public class CriteriaBuilderStaticAccessor {
    /**
     * 用于{@link org.springframework.context.annotation.DependsOn}注解使用
     */
    public static final String BEAN = "criteriaBuilderStaticAccessor";

    /**
     * 实际的builder
     */
    private static CriteriaBuilder builder;

    public CriteriaBuilderStaticAccessor(CriteriaBuilder builder) {
        CriteriaBuilderStaticAccessor.builder = builder;
    }

    public static CriteriaBuilder builder() {
        Assert.notNull(builder, "builder not initialized");
        return builder;
    }
}
