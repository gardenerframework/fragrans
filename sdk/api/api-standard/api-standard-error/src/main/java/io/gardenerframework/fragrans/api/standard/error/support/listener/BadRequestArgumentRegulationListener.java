package io.gardenerframework.fragrans.api.standard.error.support.listener;

import io.gardenerframework.fragrans.api.standard.error.exception.client.BadRequestArgumentException;
import io.gardenerframework.fragrans.api.standard.error.support.DefaultApiErrorFactory;
import io.gardenerframework.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理spring的controller上的参数异常
 *
 * @author zhanghan30
 * @date 2022/5/9 9:51 下午
 * @see org.springframework.web.bind.MethodArgumentNotValidException
 * @see org.springframework.validation.BindException
 */
@Component
@AllArgsConstructor
@ConditionalOnBean(DefaultApiErrorFactory.class)
@Order(0)
public class BadRequestArgumentRegulationListener implements ApplicationListener<InitializingApiErrorPropertiesEvent> {
    private final EnhancedMessageSource messageSource;

    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        if (event.getError() instanceof BindException && event.getApiError().getStatus() == HttpStatus.BAD_REQUEST.value()) {
            event.getApiError().setError(BadRequestArgumentException.class.getCanonicalName());
            event.getApiError().setMessage(messageSource.getMessage(BadRequestArgumentException.class, event.getLocale()));
            if (((BindException) event.getError()).getFieldErrorCount() > 0) {
                //将参数错误的内容输出
                Map<String, Object> details = new HashMap<>(((BindException) event.getError()).getFieldErrorCount());
                ((BindException) event.getError()).getFieldErrors().forEach(
                        fieldError -> details.put(fieldError.getField(), fieldError.getDefaultMessage())
                );
                event.getApiError().setDetails(details);
            }
        }
    }
}
