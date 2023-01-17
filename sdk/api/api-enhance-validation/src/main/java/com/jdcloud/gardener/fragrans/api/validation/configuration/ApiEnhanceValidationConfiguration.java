package com.jdcloud.gardener.fragrans.api.validation.configuration;

import com.jdcloud.gardener.fragrans.api.validation.ApiEnhanceValidationSupport;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validator;

/**
 * api追踪自动配置
 *
 * @author ZhangHan
 * @date 2021/8/21 5:00
 */
@Configuration
@AutoConfigureAfter(ValidationAutoConfiguration.class)
public class ApiEnhanceValidationConfiguration {
    @Bean
    public ApiEnhanceValidationSupport pathVariableAndRequestParamValidationAdvice(
            Validator validator
    ) {
        return new ApiEnhanceValidationSupport(validator);
    }
}
