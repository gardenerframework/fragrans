package io.gardenerframework.fragrans.api.standard.error.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

/**
 * 当集成了spring security时的自动配置
 *
 * @author zhanghan
 * @date 2021/8/19 18:26
 */
@Configuration
@ConditionalOnClass(WebSecurityCustomizer.class)
public class ApiStandardErrorSecurityConfiguration {
    @Value("${server.error.path:${error.path:/error}}")
    private String errorPageUrl;

    @Bean
    public WebSecurityCustomizer fragransWebSecurityCustomizer() {
        return web -> web.ignoring().antMatchers(errorPageUrl);
    }
}
