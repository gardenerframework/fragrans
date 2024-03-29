package io.gardenerframework.fragrans.api.validation.configuration;

import io.gardenerframework.fragrans.api.validation.HandlerMethodArgumentBeanValidator;
import io.gardenerframework.fragrans.api.validation.HandlerMethodArgumentsEnhanceValidationSupport;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * api追踪自动配置
 *
 * @author ZhangHan
 * @date 2021/8/21 5:00
 */
@Configuration
@AutoConfigureAfter(ValidationAutoConfiguration.class)
@Import({HandlerMethodArgumentsEnhanceValidationSupport.class, HandlerMethodArgumentBeanValidator.class})
public class ApiEnhanceValidationConfiguration {
}
