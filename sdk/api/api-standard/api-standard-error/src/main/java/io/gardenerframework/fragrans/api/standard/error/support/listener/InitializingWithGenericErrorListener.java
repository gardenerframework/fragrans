package io.gardenerframework.fragrans.api.standard.error.support.listener;

import io.gardenerframework.fragrans.api.standard.error.DefaultApiErrorConstants;
import io.gardenerframework.fragrans.api.standard.error.configuration.ApiStandardErrorComponent;
import io.gardenerframework.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author zhanghan30
 * @date 2022/5/9 9:40 下午
 */
@ApiStandardErrorComponent
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
public class InitializingWithGenericErrorListener implements ApplicationListener<InitializingApiErrorPropertiesEvent> {
    private final EnhancedMessageSource messageSource;

    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        //先初始化为模糊错误
        event.getApiError().setError(DefaultApiErrorConstants.GENERIC_ERROR);
        event.getApiError().setMessage(messageSource.getMessage(DefaultApiErrorConstants.GENERIC_ERROR, (Object[]) null, event.getLocale()));
    }
}
