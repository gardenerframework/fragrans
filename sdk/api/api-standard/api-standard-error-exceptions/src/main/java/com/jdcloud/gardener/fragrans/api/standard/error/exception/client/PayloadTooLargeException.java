package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 请求体过长导致错误
 *
 * @author zhanghan
 * @date 2020-11-12 20:06
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
@HttpStatusRepresentative
public class PayloadTooLargeException extends ApiStandardExceptions.ClientSideException {
    public PayloadTooLargeException() {
    }

    public PayloadTooLargeException(String message) {
        super(message);
    }

    public PayloadTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayloadTooLargeException(Throwable cause) {
        super(cause);
    }

    public PayloadTooLargeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
