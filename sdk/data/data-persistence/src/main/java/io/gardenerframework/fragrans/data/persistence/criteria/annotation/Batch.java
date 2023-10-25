package io.gardenerframework.fragrans.data.persistence.criteria.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 执行批量，批量做什么由具体的操作性注解决定
 *
 * @author ZhangHan
 * @date 2022/11/28 13:50
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Batch {
}
