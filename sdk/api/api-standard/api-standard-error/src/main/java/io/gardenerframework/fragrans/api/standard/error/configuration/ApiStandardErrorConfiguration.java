package io.gardenerframework.fragrans.api.standard.error.configuration;

import io.gardenerframework.fragrans.api.standard.error.ApiStandardErrorPackage;
import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/6/24 7:39 下午
 */
@Configuration
@ComponentScan(basePackageClasses = ApiStandardErrorPackage.class)
@RevealError(superClasses = {
        ApiStandardExceptions.ClientSideException.class,
        ApiStandardExceptions.ServerSideException.class
})
public class ApiStandardErrorConfiguration {
}
