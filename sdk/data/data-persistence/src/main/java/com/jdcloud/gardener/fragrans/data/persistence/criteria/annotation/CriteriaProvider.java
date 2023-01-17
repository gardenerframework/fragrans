package com.jdcloud.gardener.fragrans.data.persistence.criteria.annotation;

import com.jdcloud.gardener.fragrans.data.persistence.criteria.annotation.factory.CriteriaFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解到注解或者字段上
 *
 * @author ZhangHan
 * @date 2022/11/28 13:50
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface CriteriaProvider {
    /**
     * 使用哪种条件工厂
     *
     * @return 工厂类型
     */
    Class<? extends CriteriaFactory> value();
}
