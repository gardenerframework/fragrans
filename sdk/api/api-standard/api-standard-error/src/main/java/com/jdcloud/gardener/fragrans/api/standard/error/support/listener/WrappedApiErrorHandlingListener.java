package com.jdcloud.gardener.fragrans.api.standard.error.support.listener;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiErrorWrappingException;
import com.jdcloud.gardener.fragrans.api.standard.error.support.DefaultApiErrorFactory;
import com.jdcloud.gardener.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 处理捕捉到的{@link ApiErrorWrappingException}
 *
 * @author zhanghan30
 * @date 2022/5/9 6:45 下午
 */
@Component
@ConditionalOnBean(DefaultApiErrorFactory.class)
@Order(0)
public class WrappedApiErrorHandlingListener implements ApplicationListener<InitializingApiErrorPropertiesEvent> {
    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        if (event.getError() instanceof ApiErrorWrappingException) {
            //复制所有属性
            event.getApiError().setError(((ApiErrorWrappingException) event.getError()).getError());
            event.getApiError().setMessage(((ApiErrorWrappingException) event.getError()).getMessage());
            event.getApiError().setStatus(((ApiErrorWrappingException) event.getError()).getStatus().value());
            event.getApiError().setDetails(((ApiErrorWrappingException) event.getError()).getDetails());
        }
    }
}