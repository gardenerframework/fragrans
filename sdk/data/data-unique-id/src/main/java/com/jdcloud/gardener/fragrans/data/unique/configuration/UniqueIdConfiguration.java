package com.jdcloud.gardener.fragrans.data.unique.configuration;

import com.jdcloud.gardener.fragrans.data.unique.HostIdGenerator;
import com.jdcloud.gardener.fragrans.data.unique.IpAddressHoseIdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/9/23 15:23
 */
@Configuration
public class UniqueIdConfiguration {
    @Bean
    @ConditionalOnMissingBean(HostIdGenerator.class)
    public HostIdGenerator hostIdGenerator() {
        return new IpAddressHoseIdGenerator();
    }
}
