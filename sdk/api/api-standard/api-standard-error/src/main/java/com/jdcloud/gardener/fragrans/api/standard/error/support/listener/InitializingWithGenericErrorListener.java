package com.jdcloud.gardener.fragrans.api.standard.error.support.listener;

import com.jdcloud.gardener.fragrans.api.standard.error.DefaultApiErrorConstants;
import com.jdcloud.gardener.fragrans.api.standard.error.support.DefaultApiErrorFactory;
import com.jdcloud.gardener.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import com.jdcloud.gardener.fragrans.messages.EnhancedMessageSource;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/5/9 9:40 下午
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@ConditionalOnBean(DefaultApiErrorFactory.class)
public class InitializingWithGenericErrorListener implements ApplicationListener<InitializingApiErrorPropertiesEvent> {
    private final EnhancedMessageSource messageSource;

    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        //先初始化为模糊错误
        event.getApiError().setError(DefaultApiErrorConstants.GENERIC_ERROR);
        event.getApiError().setMessage(messageSource.getMessage(DefaultApiErrorConstants.GENERIC_ERROR, (Object[]) null, event.getLocale()));
    }
}
