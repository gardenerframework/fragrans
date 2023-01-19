package io.gardenerframework.fragrans.api.options.persistence.configuration;

import io.gardenerframework.fragrans.api.options.persistence.ReadonlyApiOptionPersistenceService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/9/23 15:53
 */
@Import(ReadonlyApiOptionPersistenceService.class)
@Configuration
public class ApiOptionReadonlyPersistenceConfiguration {
}
