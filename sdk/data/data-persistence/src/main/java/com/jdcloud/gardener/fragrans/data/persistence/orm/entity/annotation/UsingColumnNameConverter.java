package com.jdcloud.gardener.fragrans.data.persistence.orm.entity.annotation;

import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.converter.CamelToUnderscoreConverter;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.converter.ColumnNameConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用那种字段到列名的转换器
 *
 * @author zhanghan30
 * @date 2022/6/14 5:20 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UsingColumnNameConverter {
    Class<? extends ColumnNameConverter> value() default CamelToUnderscoreConverter.class;
}
