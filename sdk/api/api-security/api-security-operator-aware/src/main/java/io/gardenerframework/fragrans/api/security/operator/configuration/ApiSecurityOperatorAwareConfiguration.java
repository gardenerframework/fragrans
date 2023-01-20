package io.gardenerframework.fragrans.api.security.operator.configuration;

import io.gardenerframework.fragrans.api.security.operator.schema.OperatorBrief;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class ApiSecurityOperatorAwareConfiguration {
    /**
     * 生成请求范围的bean
     *
     * @return bean
     */
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public OperatorBrief operatorBrief() {
        return new OperatorBrief();
    }
}
