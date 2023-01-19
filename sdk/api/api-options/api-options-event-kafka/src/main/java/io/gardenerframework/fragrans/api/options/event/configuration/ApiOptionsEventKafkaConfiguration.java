package io.gardenerframework.fragrans.api.options.event.configuration;

import io.gardenerframework.fragrans.api.options.event.ApiOptionsEventKafkaPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/5/10 9:36 上午
 */
@Configuration
@ComponentScan(basePackageClasses = ApiOptionsEventKafkaPackage.class)
public class ApiOptionsEventKafkaConfiguration {
}
