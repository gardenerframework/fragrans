package io.gardenerframework.fragrans.data.persistence.template.support;

import io.gardenerframework.fragrans.data.persistence.configuration.DataPersistenceComponent;
import io.gardenerframework.fragrans.data.persistence.configuration.MapperBeanDefinitionProcessorAdapter;
import io.gardenerframework.fragrans.data.persistence.template.annotation.DomainDaoTemplate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/11/2 10:32
 */
@DataPersistenceComponent
public class DomainDaoTemplateRegistryInitializer extends MapperBeanDefinitionProcessorAdapter {
    @Override
    public void process(String beanName, BeanDefinition beanDefinition, @Nullable Class<?> mapperType) {
        //获取当前接口的所有模板类型
        Collection<Class<?>> templateTypes;
        if (mapperType != null) {
            templateTypes = DomainDaoTemplateTypesResolver.resolveTemplateTypes(mapperType);
            if (!CollectionUtils.isEmpty(templateTypes)) {
                templateTypes.forEach(
                        templateType -> {
                            DomainDaoTemplate annotation = templateType.getDeclaredAnnotation(DomainDaoTemplate.class);
                            Assert.notNull(annotation, templateType + " did not have @DomainDaoTemplate annotation");
                            //注册
                            DomainDaoTemplateRegistry.addItem(templateType, mapperType, beanDefinition.isPrimary());
                        }
                );
            }
        }
    }
}
