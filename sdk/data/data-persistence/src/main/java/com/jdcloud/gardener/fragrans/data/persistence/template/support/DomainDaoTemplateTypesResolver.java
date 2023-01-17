package com.jdcloud.gardener.fragrans.data.persistence.template.support;

import com.jdcloud.gardener.fragrans.data.persistence.template.annotation.DomainDaoTemplate;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZhangHan
 * @date 2022/11/2 10:59
 */
public class DomainDaoTemplateTypesResolver {
    private static final Map<Class<?>, Collection<Class<?>>> cache = new ConcurrentHashMap<>();

    /**
     * 基于给定类型解析其使用的所有dao模板
     *
     * @param target 类型
     * @return dao模板清单
     */
    public static Collection<Class<?>> resolveTemplateTypes(@NonNull Class<?> target) {
        Collection<Class<?>> classes = new HashSet<>();
        if (target.getDeclaredAnnotation(DomainDaoTemplate.class) != null) {
            classes.add(target);
        }
        classes.addAll(resolveSuperclassAndInterfaces(target));
        return classes;
    }

    /**
     * 解析给定类的所有父类和接口
     *
     * @param clazz 类型
     * @return dao模板清单
     */
    private static Collection<Class<?>> resolveSuperclassAndInterfaces(@NonNull Class<?> clazz) {
        //解析结果
        Collection<Class<?>> classes = new HashSet<>();
        //继续解析的父类和接口清单
        Collection<Class<?>> superclassAndInterfaces = new HashSet<>();
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            superclassAndInterfaces.add(superclass);
            if (superclass.getDeclaredAnnotation(DomainDaoTemplate.class) != null) {
                classes.add(superclass);
            }
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null && interfaces.length > 0) {
            superclassAndInterfaces.addAll(Arrays.asList(interfaces));
            for (Class<?> _interface : interfaces) {
                if (_interface.getDeclaredAnnotation(DomainDaoTemplate.class) != null) {
                    classes.add(_interface);
                }
            }
        }
        superclassAndInterfaces.forEach(
                superclassOrInterface -> classes.addAll(resolveSuperclassAndInterfaces(superclassOrInterface))
        );
        return classes;
    }
}
