package com.jdcloud.gardener.fragrans.data.persistence.annotation;

import com.jdcloud.gardener.fragrans.log.GenericOperationLogger;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Change;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author zhanghan30
 * @date 2022/9/23 00:42
 */
@Component
@Slf4j
public class OverrideSqlProviderAnnotationSupport implements BeanPostProcessor {
    private final GenericOperationLogger operationLogger = new GenericOperationLogger();

    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MapperFactoryBean) {
            Configuration configuration = ((MapperFactoryBean<?>) bean).getSqlSession().getConfiguration();
            Class<?> mapperInterface = ((MapperFactoryBean<?>) bean).getMapperInterface();
            OverrideSqlProviderAnnotation annotation = mapperInterface.getAnnotation(OverrideSqlProviderAnnotation.class);
            if (annotation != null) {
                Class<?> templateClass = annotation.value();

                for (Method method : mapperInterface.getMethods()) {
                    //获取namespace
                    String id = mapperInterface.getName() + "." + method.getName();
                    MappedStatement mappedStatement = configuration.getMappedStatement(id);
                    SqlSource sqlSource = mappedStatement.getSqlSource();
                    if (sqlSource instanceof ProviderSqlSource) {
                        try {
                            Field modifiersField = Field.class.getDeclaredField("modifiers");
                            modifiersField.setAccessible(true);
                            //覆盖provider type
                            Field providerTypeField = ProviderSqlSource.class.getDeclaredField("providerType");
                            providerTypeField.setAccessible(true);
                            modifiersField.setInt(providerTypeField, providerTypeField.getModifiers() & ~Modifier.FINAL);
                            providerTypeField.set(sqlSource, templateClass);
                            //覆盖provider method
                            Field providerMethodField = ProviderSqlSource.class.getDeclaredField("providerMethod");
                            providerMethodField.setAccessible(true);
                            modifiersField.setInt(providerMethodField, providerMethodField.getModifiers() & ~Modifier.FINAL);
                            Method originalMethod = (Method) providerMethodField.get(sqlSource);
                            Method newMethod = templateClass.getMethod(originalMethod.getName(), originalMethod.getParameterTypes());
                            providerMethodField.set(sqlSource, newMethod);
                            operationLogger.info(
                                    log,
                                    GenericOperationLogContent.builder()
                                            .what(ProviderSqlSource.class)
                                            .state(new Done())
                                            .operation(new Change())
                                            .detail(new Detail() {
                                                private Method before = originalMethod;
                                                private Method after = newMethod;
                                            }).build(),
                                    null
                            );
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
            }
        }
        return bean;
    }
}