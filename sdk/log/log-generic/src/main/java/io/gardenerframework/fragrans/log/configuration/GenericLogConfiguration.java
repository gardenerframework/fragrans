package io.gardenerframework.fragrans.log.configuration;

import io.gardenerframework.fragrans.log.GenericLogPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/6/9 3:50 下午
 */
@Configuration
@ComponentScan(basePackageClasses = GenericLogPackage.class)
public class GenericLogConfiguration {
}
