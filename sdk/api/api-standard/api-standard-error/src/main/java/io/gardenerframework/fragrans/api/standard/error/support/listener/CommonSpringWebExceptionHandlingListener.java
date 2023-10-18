package io.gardenerframework.fragrans.api.standard.error.support.listener;

import io.gardenerframework.fragrans.api.standard.error.configuration.ApiStandardErrorComponent;
import io.gardenerframework.fragrans.api.standard.error.exception.client.*;
import io.gardenerframework.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ClassUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理一些常见的spring web 的错误
 *
 * @author zhanghan30
 * @date 2022/5/9 10:01 下午
 */
@ApiStandardErrorComponent
@AllArgsConstructor
@Order(0)
public class CommonSpringWebExceptionHandlingListener implements ApplicationListener<InitializingApiErrorPropertiesEvent> {
    private static final Map<Class<?>, Class<? extends RuntimeException>> SPRING_EXCEPTION_MAP = new ConcurrentHashMap<>();

    static {
        SPRING_EXCEPTION_MAP.put(HttpMediaTypeNotSupportedException.class, UnsupportedMediaTypeException.class);
        SPRING_EXCEPTION_MAP.put(HttpMediaTypeNotAcceptableException.class, NotAcceptableException.class);
        SPRING_EXCEPTION_MAP.put(HttpRequestMethodNotSupportedException.class, MethodNotAllowedException.class);
        SPRING_EXCEPTION_MAP.put(HttpMessageNotReadableException.class, BadRequestBodyException.class);
        SPRING_EXCEPTION_MAP.put(MissingServletRequestParameterException.class, BadRequestArgumentException.class);
        SPRING_EXCEPTION_MAP.put(MethodArgumentTypeMismatchException.class, BadRequestArgumentException.class);
    }

    private final EnhancedMessageSource messageSource;

    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        if (event.getError() != null && SPRING_EXCEPTION_MAP.get(ClassUtils.getUserClass(event.getError())) != null) {
            Class<? extends RuntimeException> clazz = SPRING_EXCEPTION_MAP.get(ClassUtils.getUserClass(event.getError()));
            event.getApiError().setError(clazz.getCanonicalName());
            event.getApiError().setMessage(messageSource.getMessage(clazz, event.getLocale()));
        }
    }
}
