package io.gardenerframework.fragrans.api.group.configuration;

import io.gardenerframework.fragrans.api.group.ApiGroupPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangHan
 * @date 2022/5/10 19:24
 */
@Configuration
@ComponentScan(basePackageClasses = ApiGroupPackage.class, includeFilters = @ComponentScan.Filter(ApiGroupComponent.class))
public class ApiGroupCoreConfiguration {
}
