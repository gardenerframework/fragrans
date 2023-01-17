package com.jdcloud.gardener.fragrans.api.security.configuration;

import com.jdcloud.gardener.fragrans.api.security.ApiSecurityOperatorAutoLoggingPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2021/12/1 6:39 下午
 */
@Configuration
@ComponentScan(basePackageClasses = ApiSecurityOperatorAutoLoggingPackage.class)
public class ApiSecurityOperatorAutoLoggingConfiguration {
}