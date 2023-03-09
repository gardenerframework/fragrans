package io.gardenerframework.fragrans.api.security.automation.data.configuration;

import io.gardenerframework.fragrans.api.security.automation.data.advice.OperatorInjector;
import io.gardenerframework.fragrans.api.security.operator.schema.OperatorBrief;
import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2023/2/3 13:12
 */
@Configuration
@ConditionalOnClass({
        OperatorBrief.class,
        SecurityTraits.AuditingTraits.IdentifierTraits.Operator.class
})
public class OperatorInjectionConfiguration {
    @Bean

    public OperatorInjector operatorInjector(OperatorBrief operatorBrief) {
        return new OperatorInjector(operatorBrief);
    }
}
