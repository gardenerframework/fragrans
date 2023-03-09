package io.gardenerframework.fragrans.api.standard.error.support.listener;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiErrorDetailsSupplier;
import io.gardenerframework.fragrans.api.standard.error.support.DefaultApiErrorFactory;
import io.gardenerframework.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/5/9 11:07 下午
 */
@Component
@ConditionalOnBean(DefaultApiErrorFactory.class)
@Order(0)
public class ApiErrorDetailsSupplierHandlingListener implements ApplicationListener<InitializingApiErrorPropertiesEvent> {
    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        if (event.getError() instanceof ApiErrorDetailsSupplier) {
            event.getApiError().setDetails(((ApiErrorDetailsSupplier) event.getError()).getDetails());
        }
    }
}
