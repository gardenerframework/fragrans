package io.gardenerframework.fragrans.data.persistence.template.support;

import io.gardenerframework.fragrans.data.persistence.template.annotation.DomainObjectTemplate;
import lombok.NonNull;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link DomainObjectTemplate}的工具类
 *
 * @author zhanghan30
 * @date 2022/10/27 14:32
 */
public class DomainObjectTemplateTypesResolver {
    /**
     * 缓存，省的每次都要解析
     * dao目标类型 -> dao实现的模板类型 -> 对象模板类型 -> 实现类型
     */
    private static final Map<Class<?>, Map<Class<?>, Map<Class<?>, Class<?>>>> templateImplementationTypesCache = new ConcurrentHashMap<>();
    /**
     * 类型扫描出的所有对象模板类型的 dao模板类型 -> 对项模板类型清单
     */
    private static final Map<Class<?>, Collection<Class<?>>> templateTypesCache = new ConcurrentHashMap<>();


    /**
     * 扫描给定类型的泛型参数，获取带有{@link DomainObjectTemplate}注解的类型
     *
     * @param daoTemplate 目标类型
     * @return 扫描出来的所有类型
     */
    public static Collection<Class<?>> resolveTemplateTypes(@NonNull Class<?> daoTemplate) {
        //读缓存
        Collection<Class<?>> types = templateTypesCache.get(daoTemplate);
        if (types != null) {
            return types;
        }
        //缓存没有，执行扫描
        types = new HashSet<>();
        for (TypeVariable<? extends Class<?>> typeParameter : daoTemplate.getTypeParameters()) {
            for (Type bound : typeParameter.getBounds()) {
                Assert.isTrue(bound instanceof Class, bound + " is not Class");
                if (((Class<?>) bound).getDeclaredAnnotation(DomainObjectTemplate.class) != null) {
                    types.add((Class<?>) bound);
                }
            }
        }
        //缓存结果
        templateTypesCache.put(daoTemplate, types);
        return types;
    }


    /**
     * 扫描给定目标基于给定模板的所有业务对象模板的实现类
     *
     * @param target      目标类型
     * @param dapTemplate 模板类型
     * @return 对象模板实现类与模板类型的映射，为{@code null}则证明对应的模板在当前类型下没有任何实现类
     */
    @Nullable
    public static Map<Class<?>, Class<?>> resolveTemplateImplementationTypeMappings(@NonNull Class<?> target, @NonNull Class<?> dapTemplate) {
        //从缓存中获取当前target对应的item
        Map<Class<?>, Map<Class<?>, Class<?>>> item = templateImplementationTypesCache.get(target);
        //2级访问模板类型
        Map<Class<?>, Class<?>> mappings = (item == null ? null : item.get(dapTemplate));
        if (mappings != null) {
            return mappings;
        }
        //mapping没有，需要重新解析
        mappings = resolveGenericType(target, dapTemplate);
        if (item == null) {
            item = new HashMap<>();
            //将空的item设置进去
            templateImplementationTypesCache.put(target, item);
        }
        //解析万的结果放入缓存中
        //fix 之前存入的类型是target
        item.put(dapTemplate, mappings);
        return mappings;
    }


    /**
     * 解析范型
     * <p>
     * 逻辑是从当前类开始电柜自己的父类和接口，找到第一个满足模板类型的实现，解析mapping后返回
     *
     * @param type 范型类型
     * @return 记录模板参数
     */
    @Nullable
    private static Map<Class<?>, Class<?>> resolveGenericType(@Nullable Type type, @NonNull Class<?> template) {
        Collection<Class<?>> templateTypes = resolveTemplateTypes(template);
        if (CollectionUtils.isEmpty(templateTypes)) {
            //模板没有任何模板类型，那当前也就不需要找到任何实现类型
            return null;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (template.equals(rawType)) {
                Map<Class<?>, Class<?>> mappings = new HashMap<>();
                //找到了模板泛型的实现类
                for (Type actualTypeArgument : ((ParameterizedType) type).getActualTypeArguments()) {
                    Assert.isTrue(actualTypeArgument instanceof Class, actualTypeArgument + " is not a Class");
                    templateTypes.forEach(
                            templateType -> {
                                //类型是当前模板类型的一个子集
                                if (templateType.isAssignableFrom((Class<?>) actualTypeArgument)) {
                                    mappings.put(templateType, (Class<?>) actualTypeArgument);
                                }
                            }
                    );
                }
                //将找到的结果返回
                return mappings;
            } else {
                //实现的泛型不是所需模板类型
                type = rawType;
            }
        }
        if (type instanceof Class) {
            Collection<Type> genericTypes = new HashSet<>();
            Type genericSuperclass = ((Class<?>) type).getGenericSuperclass();
            if (genericSuperclass != null) {
                genericTypes.add(genericSuperclass);
            }
            genericTypes.addAll(Arrays.asList(((Class<?>) type).getGenericInterfaces()));
            for (Type genericType : genericTypes) {
                //递归查找所有范型
                Map<Class<?>, Class<?>> mappings = resolveGenericType(genericType, template);
                if (mappings != null) {
                    return mappings;
                }
            }
        }
        return null;
    }


    @Nullable
    public static <A> Class<? extends A> resolveTemplateImplementationType(@NonNull ProviderContext context, @NonNull Class<?> daoTemplate, Class<A> objectTemplate) {
        return resolveTemplateImplementationType(context.getMapperType(), daoTemplate, objectTemplate);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <A> Class<? extends A> resolveTemplateImplementationType(@NonNull Class<?> target, @NonNull Class<?> daoTemplate, Class<A> objectTemplate) {
        Map<Class<?>, Class<?>> mappings = resolveTemplateImplementationTypeMappings(target, daoTemplate);
        return mappings == null ? null : (Class<? extends A>) mappings.get(objectTemplate);
    }
}
