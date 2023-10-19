package io.gardenerframework.fragrans.api.security.automation.configuration;

import io.gardenerframework.fragrans.api.security.automation.log.customizer.GenericOperationLoggerMessageCustomizer;
import io.gardenerframework.fragrans.api.security.operator.schema.OperatorBrief;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2021/12/1 6:39 下午
 */
@Configuration
@ConditionalOnClass({
        GenericOperationLogger.class,
        OperatorBrief.class
})
public class ApiSecurityAutomationLoggingConfiguration {
    @Bean
    public GenericOperationLoggerMessageCustomizer genericOperationLoggerMessageCustomizer(OperatorBrief operatorBrief) {
        return new GenericOperationLoggerMessageCustomizer(operatorBrief);
    }
}
