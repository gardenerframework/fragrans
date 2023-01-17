package com.jdcloud.gardener.fragrans.api.standard.error.exception.server;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * http版本不支持
 *
 * @author zhanghan
 * @date 2020-11-12 20:27
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.HTTP_VERSION_NOT_SUPPORTED)
@HttpStatusRepresentative
public class HttpVersionNotSupportedException extends ApiStandardExceptions.ServerSideException {
    public HttpVersionNotSupportedException() {
    }

    public HttpVersionNotSupportedException(String message) {
        super(message);
    }

    public HttpVersionNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpVersionNotSupportedException(Throwable cause) {
        super(cause);
    }

    public HttpVersionNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
