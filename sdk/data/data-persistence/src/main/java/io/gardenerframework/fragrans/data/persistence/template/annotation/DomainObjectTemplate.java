package io.gardenerframework.fragrans.data.persistence.template.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在记录类上，标记当前记录是数据库的orm存储模板
 * <p>
 * 此外{@link DomainDaoTemplate}应当在类的泛型中约定一个且仅有一个业务对象模板，如
 * <p>
 * public class SampleDaoTemplate{@literal <}E extends SampleDomainObject{@literal >} {
 * <p>
 * }
 * <p>
 * 否则在进行模板扫描的时候会报错
 *
 * @author zhanghan30
 * @date 2022/10/27 14:32
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DomainObjectTemplate {
}
