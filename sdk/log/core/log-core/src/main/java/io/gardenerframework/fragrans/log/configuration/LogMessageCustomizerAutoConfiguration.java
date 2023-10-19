package io.gardenerframework.fragrans.log.configuration;

import io.gardenerframework.fragrans.log.BasicLogger;
import io.gardenerframework.fragrans.log.LogMessageCustomizer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
@AllArgsConstructor
public class LogMessageCustomizerAutoConfiguration implements InitializingBean {
    @NonNull
    private final Collection<LogMessageCustomizer> logMessageCustomizers;

    @Override
    public void afterPropertiesSet() throws Exception {
        //自动注册bean
        logMessageCustomizers.forEach(BasicLogger::addLogMessageCustomizer);
    }
}
