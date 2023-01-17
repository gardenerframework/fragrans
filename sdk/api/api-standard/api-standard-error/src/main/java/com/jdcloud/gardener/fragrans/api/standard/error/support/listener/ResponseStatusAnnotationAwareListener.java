package com.jdcloud.gardener.fragrans.api.standard.error.support.listener;

import com.jdcloud.gardener.fragrans.api.standard.error.support.DefaultApiErrorFactory;
import com.jdcloud.gardener.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 负责处理{@link ResponseStatus}注解的错误
 * <p>
 * 注意这里特别说明了是错误，因为这个注解可不只给异常用
 *
 * @author zhanghan30
 * @date 2022/5/9 8:00 下午
 */
@Component
@ConditionalOnBean(DefaultApiErrorFactory.class)
@Order(0)
public class ResponseStatusAnnotationAwareListener implements ApplicationListener<InitializingApiErrorPropertiesEvent> {
    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        if (event.getError() != null) {
            ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ClassUtils.getUserClass(event.getError().getClass()), ResponseStatus.class);
            if (responseStatus != null) {
                //使用注解的状态
                event.getApiError().setStatus(responseStatus.value().value());
                event.getApiError().setReason(responseStatus.value().getReasonPhrase());
            }
        }
    }
}
