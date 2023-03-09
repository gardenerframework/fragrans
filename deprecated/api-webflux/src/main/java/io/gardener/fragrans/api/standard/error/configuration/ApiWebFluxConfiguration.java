package com.jdcloud.gardener.fragrans.api.standard.error.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcloud.gardener.fragrans.api.ApiErrorFactory;
import com.jdcloud.gardener.fragrans.api.reactive.ReactiveApiErrorConverter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 生成一个reactive的{@link org.springframework.boot.web.reactive.error.DefaultErrorAttributes}派生类的bean<br>
 * 注意虽然类名都一样，但这个和servlet的{@link org.springframework.boot.web.servlet.error.DefaultErrorAttributes}可不是一个类<br>
 *
 * @author zhanhan
 * @date 2020-11-13 22:31
 * @since 1.0.0
 */
@Configuration
@AutoConfigureBefore(ErrorWebFluxAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnMissingBean(ErrorAttributes.class)
public class ApiWebFluxConfiguration {

    @Bean
    public ReactiveApiErrorConverter reactiveApiErrorConverter(ApiErrorFactory apiErrorConverter, ObjectMapper mapper) {
        return new ReactiveApiErrorConverter(apiErrorConverter, mapper);
    }
}
