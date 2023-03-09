package io.gardenerframework.fragrans.api.standard.error.support.event;

import io.gardenerframework.fragrans.api.standard.schema.ApiError;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;

import java.util.Locale;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/5/9 5:50 下午
 */
@Getter
public class InitializingApiErrorPropertiesEvent extends ApplicationEvent {
    /**
     * 待设置属性的标准错误
     */
    private final transient ApiError apiError;
    /**
     * 原始的错误属性
     */
    private final transient Map<String, Object> errorAttributes;
    /**
     * 这个在事件处理的过程中是可以被前一个处理器覆盖的
     */
    @Nullable
    private final transient Object error;
    /**
     * 语言环境
     */
    private final transient Locale locale;

    public InitializingApiErrorPropertiesEvent(ApiError apiError, Map<String, Object> errorAttributes, @Nullable Object error, Locale locale) {
        super(apiError);
        this.apiError = apiError;
        this.errorAttributes = errorAttributes;
        this.error = error;
        this.locale = locale;
    }
}
