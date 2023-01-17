package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 满足请求所需的预期条件未满足
 *
 * @author zhanghan
 * @date 2020-11-12 19:59
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
@HttpStatusRepresentative
public class ExpectationFailedException extends ApiStandardExceptions.ClientSideException {
    public ExpectationFailedException() {
    }

    public ExpectationFailedException(String message) {
        super(message);
    }

    public ExpectationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpectationFailedException(Throwable cause) {
        super(cause);
    }

    public ExpectationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
