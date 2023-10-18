package io.gardenerframework.fragrans.data.persistence.orm.mapping.annotation;

import io.gardenerframework.fragrans.data.persistence.configuration.DataPersistenceComponent;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/9/25 05:09
 */
@DataPersistenceComponent
public class ColumnTypeHandlerSupport implements BeanPostProcessor {
    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MapperFactoryBean) {
            Configuration configuration = ((MapperFactoryBean<?>) bean).getSqlSession().getConfiguration();
            Class<?> mapperInterface = ((MapperFactoryBean<?>) bean).getMapperInterface();
            for (Method method : mapperInterface.getMethods()) {
                ColumnTypeHandler annotation = method.getAnnotation(ColumnTypeHandler.class);
                if (annotation != null) {
                    Class<? extends ColumnTypeHandlerProvider>[] providerClasses = annotation.provider();
                    for (Class<? extends ColumnTypeHandlerProvider> providerClass : providerClasses) {
                        try {
                            ColumnTypeHandlerProvider provider = providerClass.newInstance();
                            ColumnMapping mapping = Objects.requireNonNull(provider.provide(
                                    mapperInterface,
                                    method
                            ));
                            ResultMap resultMap = configuration.getResultMap(buildMethodId(mapperInterface, method));
                            ResultMapping resultMapping = new ResultMapping.Builder(
                                    configuration,
                                    mapping.getField(),
                                    mapping.getColumn(),
                                    mapping.getHandler()
                            ).javaType(mapping.getJavaType()).build();
                            for (String target : Arrays.asList("resultMappings", "idResultMappings", "propertyResultMappings")) {
                                addResultMappingIntoTargetMappings(resultMap, target, resultMapping);
                            }
                            Field mappedColumns = ResultMap.class.getDeclaredField("mappedColumns");
                            mappedColumns.setAccessible(true);
                            Set<String> set = new HashSet<>(resultMap.getMappedColumns());
                            set.add(mapping.getColumn().toUpperCase(Locale.ENGLISH));
                            mappedColumns.set(resultMap, Collections.unmodifiableSet(set));

                            Field mappedProperties = ResultMap.class.getDeclaredField("mappedProperties");
                            mappedProperties.setAccessible(true);
                            set = new HashSet<>(resultMap.getMappedProperties());
                            set.add(mapping.getField().toLowerCase(Locale.ENGLISH));
                            mappedProperties.set(resultMap, Collections.unmodifiableSet(set));
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
            }
        }
        return bean;
    }

    private void addResultMappingIntoTargetMappings(ResultMap resultMap, String target, ResultMapping mapping) throws Exception {
        Field field = ResultMap.class.getDeclaredField(target);
        field.setAccessible(true);
        List<ResultMapping> list = new ArrayList((Collection) field.get(resultMap));
        list.add(mapping);
        field.set(resultMap, Collections.unmodifiableList(list));
    }

    private String buildMethodId(Class<?> type, Method method) {
        Collection<Class<?>> typeUses = new ArrayList<>(Arrays.asList(method.getParameterTypes()));
        if (CollectionUtils.isEmpty(typeUses)) {
            typeUses.add(void.class);
        }
        return type.getName() + "." + method.getName() + "-" +
                typeUses.stream().map(Class::getSimpleName).collect(Collectors.joining("-"));
    }
}
