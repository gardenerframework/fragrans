package io.gardenerframework.fragrans.api.standard.error.exception.client;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiErrorDetailsSupplier;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数有问题
 *
 * @author ZhangHan
 * @date 2021/8/20 1:06
 */
public class BadRequestArgumentException extends BadRequestException implements ApiErrorDetailsSupplier {
    private Map<String, Object> details;

    public BadRequestArgumentException() {
    }

    public BadRequestArgumentException(String message) {
        super(message);
    }

    public BadRequestArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestArgumentException(Throwable cause) {
        super(cause);
    }

    public BadRequestArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BadRequestArgumentException(Map<String, Object> details) {
        this.details = details;
    }

    public BadRequestArgumentException(String message, Map<String, Object> details) {
        super(message);
        this.details = details;
    }

    public BadRequestArgumentException(String message, Throwable cause, Map<String, Object> details) {
        super(message, cause);
        this.details = details;
    }

    public BadRequestArgumentException(Throwable cause, Map<String, Object> details) {
        super(cause);
        this.details = details;
    }

    public BadRequestArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Map<String, Object> details) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.details = details;
    }

    @Override
    public Map<String, Object> getDetails() {
        return details == null ? new HashMap<>() : details;
    }
}
