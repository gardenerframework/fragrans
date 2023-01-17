package com.jdcloud.gardener.fragrans.api.options.test;

import com.jdcloud.gardener.fragrans.api.options.lifecycle.event.ApiOptionChangedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/7/15 8:45 下午
 */
@Component
public class ApiOptionChangedNotifier implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener
    public void onApiOptionChangedEvent(ApiOptionChangedEvent event) {
        if (event.getSource() == ApiOptionChangedEvent.Source.ENDPOINT) {
            applicationEventPublisher.publishEvent(new ApiOptionChangedEvent(
                    event.getId(),
                    null,
                    UUID.randomUUID().toString(),
                    ApiOptionChangedEvent.Source.NOTIFICATION
            ));
        }
    }
}
