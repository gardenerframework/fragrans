package io.gardenerframework.fragrans.data.persistence.criteria.annotation;

import io.gardenerframework.fragrans.data.persistence.criteria.annotation.factory.PrefixFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 执行前缀查询
 *
 * @author ZhangHan
 * @date 2022/11/28 13:50
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@CriteriaProvider(PrefixFactory.class)
public @interface Prefix {
}
