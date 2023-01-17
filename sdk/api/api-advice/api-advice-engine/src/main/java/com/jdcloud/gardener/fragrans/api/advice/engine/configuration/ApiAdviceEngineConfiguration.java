package com.jdcloud.gardener.fragrans.api.advice.engine.configuration;

import com.jdcloud.gardener.fragrans.api.advice.engine.ApiAdviceEnginePackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * api追踪自动配置
 *
 * @author ZhangHan
 * @date 2021/8/21 5:00
 */
@Configuration
@ComponentScan(basePackageClasses = ApiAdviceEnginePackage.class)
public class ApiAdviceEngineConfiguration {
}
