package io.gardenerframework.fragrans.validation.configuration;

import io.gardenerframework.fragrans.messages.configuration.basename.BasenameProvider;
import io.gardenerframework.fragrans.messages.support.EnhancedMessageSourceSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import java.util.Collections;
import java.util.HashSet;

/**
 * 重写了验证器的自动配置，让它在{@link ValidationAutoConfiguration}前生效<br>
 * 主要的目的就是为了统一使用spring或messages生成的{@link ResourceBundleMessageSource}的bean作为统一的消息格式化组件
 *
 * @author zhanghan
 * @date 2021/7/12 15:49
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(ValidationAutoConfiguration.class)
@ConditionalOnClass(ExecutableValidator.class)
@ConditionalOnResource(resources = "classpath:META-INF/services/javax.validation.spi.ValidationProvider")
public class EnhanceValidationConfiguration {
    /**
     * 自己生成{@link LocalValidatorFactoryBean}的验证器
     *
     * @param messageSource 消息源
     * @return bean
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(Validator.class)
    @ConditionalOnBean(value = EnhancedMessageSourceSupport.class)
    public LocalValidatorFactoryBean defaultValidator(EnhancedMessageSourceSupport messageSource) {
        //加入对MessageInterpolator的兼容
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setValidationMessageSource(messageSource);
        return factoryBean;
    }

    @Bean
    public BasenameProvider validationMessageBasenameProvider() {
        return () -> new HashSet<>(Collections.singletonList("ValidationMessages"));
    }
}
