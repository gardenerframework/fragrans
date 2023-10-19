package io.gardenerframework.fragrans.data.persistence.annotation;

import io.gardenerframework.fragrans.data.persistence.configuration.DataPersistenceComponent;
import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Change;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author zhanghan30
 * @date 2022/9/23 00:42
 */
@DataPersistenceComponent
@Slf4j
public class OverrideSqlProviderAnnotationSupport implements BeanPostProcessor {
    private final GenericOperationLogger operationLogger = GenericLoggers.operationLogger();

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