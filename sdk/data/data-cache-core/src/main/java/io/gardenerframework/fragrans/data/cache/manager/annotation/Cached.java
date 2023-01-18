package io.gardenerframework.fragrans.data.cache.manager.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在实体类上，表达这是个缓存所需的对象
 *
 * @author zhanghan30
 * @date 2022/2/11 7:17 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {
    /**
     * 使用的redis命名空间
     *
     * @return 命名空间
     */
    String[] namespaces();

    /**
     * 使用的redis后缀
     *
     * @return 后缀
     */
    String suffix();
}
