package io.gardenerframework.fragrans.data.persistence.configuration;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

/**
 * @author zhanghan30
 * @date 2022/9/25 21:12
 */
public abstract class MapperBeanDefinitionProcessorAdapter implements MapperBeanDefinitionProcessor, BeanFactoryPostProcessor {
    @Override
    public final void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beforeIterateBeanFactory();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            if (MapperFactoryBean.class.getName().equals(beanDefinition.getBeanClassName())) {
                Class<?> mapperClass = null;
                if (beanDefinition instanceof ScannedGenericBeanDefinition) {
                    try {
                        mapperClass = Class.forName(((ScannedGenericBeanDefinition) beanDefinition).getMetadata().getClassName());
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                }
                Class<?> watchingMapperClass = getWatchingMapperClass();
                if (watchingMapperClass != null && mapperClass != null && !watchingMapperClass.isAssignableFrom(mapperClass)) {
                    continue;
                }
                process(beanName, beanDefinition, mapperClass);
            }
        }
        afterIterateBeanFactory();
    }

    /**
     * 在每次开始遍历bean工厂前调用
     */
    protected void beforeIterateBeanFactory() {

    }

    /**
     * 在每次结束遍历bean工厂后调用
     */
    protected void afterIterateBeanFactory() {

    }
}
