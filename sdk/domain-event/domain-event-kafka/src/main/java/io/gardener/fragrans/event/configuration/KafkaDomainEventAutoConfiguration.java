package com.jdcloud.gardener.fragrans.event.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcloud.gardener.fragrans.event.DomainEventHelper;
import com.jdcloud.gardener.fragrans.event.KafkaDomainEventSender;
import com.jdcloud.gardener.fragrans.data.unique.HostIdGenerator;
import com.jdcloud.gardener.fragrans.data.unique.UniqueIdGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author zhanghan30
 * @date 2021/11/4 10:38 下午
 */
@Configuration
public class KafkaDomainEventAutoConfiguration {
    public KafkaDomainEventAutoConfiguration(
            HostIdGenerator hostIdGenerator,
            ObjectMapper objectMapper,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        DomainEventHelper.init(
                new KafkaDomainEventSender(kafkaTemplate, objectMapper),
                new UniqueIdGenerator(hostIdGenerator.getHostId())
        );
    }
}
