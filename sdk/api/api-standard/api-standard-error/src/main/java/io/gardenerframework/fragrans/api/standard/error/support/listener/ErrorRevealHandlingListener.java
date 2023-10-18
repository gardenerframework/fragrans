package io.gardenerframework.fragrans.api.standard.error.support.listener;

import io.gardenerframework.fragrans.api.standard.error.configuration.ApiErrorRegistry;
import io.gardenerframework.fragrans.api.standard.error.configuration.ApiStandardErrorComponent;
import io.gardenerframework.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

/**
 * 处理在业务错误定义包内的错误
 *
 * @author zhanghan30
 * @date 2022/5/9 9:12 下午
 */
@ApiStandardErrorComponent
@Slf4j
@RequiredArgsConstructor
@Order(0)
public class ErrorRevealHandlingListener implements ApplicationListener<InitializingApiErrorPropertiesEvent> {
    /**
     * 注册表
     */
    private final ApiErrorRegistry registry;
    private final EnhancedMessageSource messageSource;

    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        //判断当前错误是否要展示
        if (event.getError() != null && registry.isErrorRevealed(event.getError())) {
            //将业务错误的类名作为错误代码返回
            event.getApiError().setError(event.getError().getClass().getCanonicalName());
            //将业务错误作为消息输出返回
            event.getApiError().setMessage(messageSource.getMessage(event.getError(), event.getLocale()));
        }
    }
}
