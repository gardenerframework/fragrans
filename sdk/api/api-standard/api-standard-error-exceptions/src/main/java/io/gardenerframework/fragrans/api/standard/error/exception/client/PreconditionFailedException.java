package io.gardenerframework.fragrans.api.standard.error.exception.client;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 前置条件不满足
 *
 * @author zhanghan
 * @date 2020-11-12 20:09
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
@HttpStatusRepresentative
public class PreconditionFailedException extends ApiStandardExceptions.ClientSideException {
    public PreconditionFailedException() {
    }

    public PreconditionFailedException(String message) {
        super(message);
    }

    public PreconditionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreconditionFailedException(Throwable cause) {
        super(cause);
    }

    public PreconditionFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
