package io.gardenerframework.fragrans.data.persistence.configuration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/9/25 21:03
 */
public interface MapperBeanDefinitionProcessor {
    /**
     * 当前什么类型的mapper需要注意(包含子类型)
     * 为空就是什么类型都关注
     *
     * @return 类型
     */
    @Nullable
    default Class<?> getWatchingMapperClass() {
        return null;
    }

    /**
     * 执行处理
     *
     * @param beanName       bean名称
     * @param beanDefinition bean定义
     * @param mapperType     映射器类型 - 当关注所有类型时，输入的就是当前类型的映射器
     */
    void process(String beanName, BeanDefinition beanDefinition, @Nullable Class<?> mapperType);
}
