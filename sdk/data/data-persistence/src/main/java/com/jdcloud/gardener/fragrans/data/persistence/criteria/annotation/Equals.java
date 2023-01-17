package com.jdcloud.gardener.fragrans.data.persistence.criteria.annotation;

import com.jdcloud.gardener.fragrans.data.persistence.criteria.annotation.factory.EqualsFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 执行判等
 *
 * @author ZhangHan
 * @date 2022/11/28 13:50
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@CriteriaProvider(EqualsFactory.class)
public @interface Equals {
}
