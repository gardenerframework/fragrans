package io.gardenerframework.fragrans.api.options.configuration;

import io.gardenerframework.fragrans.api.options.ApiOptionsEnginePackage;
import io.gardenerframework.fragrans.api.options.schema.ApiOptionsRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/1/3 4:23 下午
 */
@Configuration
@Slf4j
@ComponentScan(basePackageClasses = ApiOptionsEnginePackage.class, includeFilters = @ComponentScan.Filter(ApiOptionsEngineComponent.class))
@Import(ApiOptionsRegistry.class)
public class ApiOptionsEngineConfiguration {
    //todo 没有办法解决api选项bean生成后到被初始化器初始化之前的差异时间内使用造成的选项数据不一致的问题
}
