package io.gardenerframework.fragrans.log.configuration;

import io.gardenerframework.fragrans.log.BasicLogger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/6/8 3:10 下午
 */
@Configuration
@Import(BasicLogger.class)
public class LogEngineConfiguration {
}
