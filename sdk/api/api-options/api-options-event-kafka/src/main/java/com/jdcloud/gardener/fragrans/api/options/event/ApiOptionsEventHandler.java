package com.jdcloud.gardener.fragrans.api.options.event;

import com.jdcloud.gardener.fragrans.api.options.lifecycle.event.ApiOptionChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 监听api 选项的事件并做出对应的动作
 *
 * @author zhanghan30
 * @date 2022/7/19 2:30 下午
 */
@Component
@RequiredArgsConstructor
public class ApiOptionsEventHandler implements ApplicationEventPublisherAware {
    public static final String API_OPTIONS_NOTIFICATION_TOPIC = "api-options";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = API_OPTIONS_NOTIFICATION_TOPIC)
    public void onApiOptionsNotification(String rawData) throws IOException, ClassNotFoundException {
        ByteArrayInputStream stream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.UTF_8));
        Object event = new ObjectInputStream(stream).readObject();
        //对于收取的应用选项变更事件，转为应用事件处理
        if (event instanceof ApiOptionChangedEvent) {
            //这里注意，如果接收到自己发送的kafka消息，上层会判断因为发送者一样从而忽略
            this.eventPublisher.publishEvent(event);
        }
    }

    /**
     * 接收到api选项变更事件
     *
     * @param event 事件
     * @throws IOException io异常
     */
    @EventListener
    public void onApiChangedEvent(ApiOptionChangedEvent event) throws IOException {
        if (event.getSource() != ApiOptionChangedEvent.Source.ENDPOINT) {
            //对于不是当前api接口引起的变动不发送kafka消息
            return;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //生成通知
        ApiOptionChangedEvent notification = new ApiOptionChangedEvent();
        notification.setId(event.getId());
        //通知中不包含已经变更的数据
        notification.setOption(null);
        //通知中指定变更选项的实例id
        notification.setInstanceId(event.getInstanceId());
        //标记来源为通知。即通知其它实例更新选项
        notification.setSource(ApiOptionChangedEvent.Source.NOTIFICATION);
        new ObjectOutputStream(stream).writeObject(notification);
        kafkaTemplate.send(API_OPTIONS_NOTIFICATION_TOPIC, stream.toString());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
