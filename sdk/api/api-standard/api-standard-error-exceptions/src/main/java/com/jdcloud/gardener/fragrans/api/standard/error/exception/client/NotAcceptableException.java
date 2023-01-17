package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 请求资源类型不接受
 *
 * @author zhanghan
 * @date 2020-11-12 20:03
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
@HttpStatusRepresentative
public class NotAcceptableException extends ApiStandardExceptions.ClientSideException {
    public NotAcceptableException() {
    }

    public NotAcceptableException(String message) {
        super(message);
    }

    public NotAcceptableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAcceptableException(Throwable cause) {
        super(cause);
    }

    public NotAcceptableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
