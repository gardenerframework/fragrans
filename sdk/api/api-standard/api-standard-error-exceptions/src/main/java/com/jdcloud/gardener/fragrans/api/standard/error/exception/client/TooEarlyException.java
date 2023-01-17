package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * EarlyData异常
 *
 * @author zhanghan
 * @date 2020-11-12 20:17
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.TOO_EARLY)
@HttpStatusRepresentative
public class TooEarlyException extends ApiStandardExceptions.ClientSideException {
    public TooEarlyException() {
    }

    public TooEarlyException(String message) {
        super(message);
    }

    public TooEarlyException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooEarlyException(Throwable cause) {
        super(cause);
    }

    public TooEarlyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
