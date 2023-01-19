package io.gardenerframework.fragrans.api.standard.error.support;

import io.gardenerframework.fragrans.api.standard.error.ApiErrorFactory;
import io.gardenerframework.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import io.gardenerframework.fragrans.api.standard.schema.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Map;

/**
 * 创建Api错误对象
 *
 * @author zhanghan30
 * @date 2020/11/9 16:28
 * @since 1.3.1
 */
@Slf4j
@Component
@ConditionalOnMissingBean(value = ApiErrorFactory.class, ignored = DefaultApiErrorFactory.class)
public class DefaultApiErrorFactory implements ApiErrorFactory, ApplicationEventPublisherAware {
    private ApplicationEventPublisher eventPublisher;

    @Override
    public ApiError createApiError(Map<String, Object> errorAttributes, @Nullable Object error, Locale locale) {
        //有些异常的报错是内嵌的
        Object nestedError = error;
        //对于状态码包装的异常戒行解包
        if (error instanceof ResponseStatusException) {
            //只解一层不惯毛病
            nestedError = ((ResponseStatusException) error).getCause();
        }
        ApiError apiError = new ApiError();
        //核心逻辑就是交由初始化的监听器去完成
        this.eventPublisher.publishEvent(new InitializingApiErrorPropertiesEvent(
                apiError,
                errorAttributes,
                nestedError,
                locale
        ));
        if (error instanceof ResponseStatusException) {
            //最终由外部异常的状态覆盖
            apiError.setStatus(((ResponseStatusException) error).getStatus().value());
            apiError.setReason(((ResponseStatusException) error).getStatus().getReasonPhrase());
        }
        return apiError;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

}
