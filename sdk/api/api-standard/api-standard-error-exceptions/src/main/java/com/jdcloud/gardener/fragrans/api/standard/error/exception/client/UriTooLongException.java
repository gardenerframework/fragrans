package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * uri过长，超过了http规范支持的最大长度
 *
 * @author zhanghan
 * @date 2020-11-12 20:23
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.URI_TOO_LONG)
@HttpStatusRepresentative
public class UriTooLongException extends ApiStandardExceptions.ClientSideException {
    public UriTooLongException() {
    }

    public UriTooLongException(String message) {
        super(message);
    }

    public UriTooLongException(String message, Throwable cause) {
        super(message, cause);
    }

    public UriTooLongException(Throwable cause) {
        super(cause);
    }

    public UriTooLongException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
