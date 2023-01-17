package com.jdcloud.gardener.fragrans.api.standard.schema.configuration;

import com.jdcloud.gardener.fragrans.api.standard.schema.ApiStandardSchemaPackage;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.support.DefaultGenericMaxPageSizeProvider;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.support.GenericMaxPageSizeProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/6/23 4:26 下午
 */
@Configuration
@ComponentScan(basePackageClasses = ApiStandardSchemaPackage.class)
public class ApiStandardSchemaConfiguration {
    @ConditionalOnMissingBean(GenericMaxPageSizeProvider.class)
    public GenericMaxPageSizeProvider genericMaxPageSizeProvider() {
        return new DefaultGenericMaxPageSizeProvider();
    }
}
