package io.gardenerframework.fragrans.messages.configuration;

import io.gardenerframework.fragrans.messages.EnhanceMessageSourcePackage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfigureBefore(MessageSourceAutoConfiguration.class)
@Slf4j
@EnableConfigurationProperties
@ComponentScan(basePackageClasses = EnhanceMessageSourcePackage.class, includeFilters = @ComponentScan.Filter(EnhanceMessageSourceComponent.class))
public class EnhanceMessageSourceConfiguration {
    /**
     * 生成配置属性，照抄自{@link MessageSourceAutoConfiguration}
     *
     * @return 配置属性
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }
}
