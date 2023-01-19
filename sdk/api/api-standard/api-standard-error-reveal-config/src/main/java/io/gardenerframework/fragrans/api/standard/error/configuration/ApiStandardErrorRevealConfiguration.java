package io.gardenerframework.fragrans.api.standard.error.configuration;

import io.gardenerframework.fragrans.api.standard.error.configuration.support.AnnotationBasesApiErrorRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/8/26 9:17 上午
 */
@Configuration
@Import(AnnotationBasesApiErrorRegistry.class)
public class ApiStandardErrorRevealConfiguration {

}
