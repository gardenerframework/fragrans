package com.jdcloud.gardener.fragrans.event;

import com.jdcloud.gardener.fragrans.event.schema.DomainEvent;

/**
 * @author zhanghan30
 * @date 2021/11/4 10:27 下午
 */
@FunctionalInterface
public interface DomainEventSender {
    /**
     * 发送领域事件
     *
     * @param topic 事件主题
     * @param event 事件
     */
    void sendEvent(String topic, DomainEvent event);
}
