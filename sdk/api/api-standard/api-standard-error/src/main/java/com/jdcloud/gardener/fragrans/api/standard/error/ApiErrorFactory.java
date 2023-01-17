package com.jdcloud.gardener.fragrans.api.standard.error;

import com.jdcloud.gardener.fragrans.api.standard.schema.ApiError;
import org.springframework.lang.Nullable;

import java.util.Locale;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/5/9 5:39 下午
 */
@FunctionalInterface
public interface ApiErrorFactory {
    /**
     * 创建api错误对象
     *
     * @param errorAttributes 错误属性
     * @param error           捕捉到的错误
     * @param locale          本地化上下文
     * @return 转换的错误
     * @see org.springframework.boot.web.servlet.error.ErrorAttributes
     * @see org.springframework.boot.web.reactive.error.ErrorAttributes
     */
    ApiError createApiError(Map<String, Object> errorAttributes, @Nullable Object error, Locale locale);
}
