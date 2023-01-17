package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 请求头部过长
 *
 * @author zhanghan
 * @date 2020-11-12 20:14
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE)
@HttpStatusRepresentative
public class RequestHeaderFieldsTooLargeException extends ApiStandardExceptions.ClientSideException {
    public RequestHeaderFieldsTooLargeException() {
    }

    public RequestHeaderFieldsTooLargeException(String message) {
        super(message);
    }

    public RequestHeaderFieldsTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestHeaderFieldsTooLargeException(Throwable cause) {
        super(cause);
    }

    public RequestHeaderFieldsTooLargeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
