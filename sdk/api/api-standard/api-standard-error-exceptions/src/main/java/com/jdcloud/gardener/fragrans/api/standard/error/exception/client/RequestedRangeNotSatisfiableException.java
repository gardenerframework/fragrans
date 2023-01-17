package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 请求范围不接受
 *
 * @author zhanghan
 * @date 2020-11-12 20:13
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
@HttpStatusRepresentative
public class RequestedRangeNotSatisfiableException extends ApiStandardExceptions.ClientSideException {
    public RequestedRangeNotSatisfiableException() {
    }

    public RequestedRangeNotSatisfiableException(String message) {
        super(message);
    }

    public RequestedRangeNotSatisfiableException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestedRangeNotSatisfiableException(Throwable cause) {
        super(cause);
    }

    public RequestedRangeNotSatisfiableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
