package io.gardenerframework.fragrans.data.persistence.template.annotation;

import org.mybatis.spring.annotation.MapperScan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在Dao模板上，用来表达这是一个Mapper模板
 * <p>
 * 模板类要求必须包含一个泛型参数，且参数的为某个带有{@link DomainObjectTemplate}注解的数据实体定义类
 * <p>
 * dao模板是不进行{@link MapperScan}的mapper
 *
 * @author zhanghan30
 * @date 2022/10/27 14:32
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DomainDaoTemplate {
}
