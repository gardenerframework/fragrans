package com.jdcloud.gardener.fragrans.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcloud.gardener.fragrans.event.schema.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author zhanghan30
 * @date 2021/11/4 10:33 下午
 */
@AllArgsConstructor
@Slf4j
public class KafkaDomainEventSender implements DomainEventSender {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void sendEvent(String topic, DomainEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            log.error("序列化事件失败", e);
            throw new RuntimeException(e);
        }
    }
}
