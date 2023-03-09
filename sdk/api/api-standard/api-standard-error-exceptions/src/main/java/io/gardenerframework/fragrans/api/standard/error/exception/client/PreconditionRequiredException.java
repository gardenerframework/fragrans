package io.gardenerframework.fragrans.api.standard.error.exception.client;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 需要前置条件
 *
 * @author zhanghan
 * @date 2020-11-12 20:10
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
@HttpStatusRepresentative
public class PreconditionRequiredException extends ApiStandardExceptions.ClientSideException {
    public PreconditionRequiredException() {
    }

    public PreconditionRequiredException(String message) {
        super(message);
    }

    public PreconditionRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreconditionRequiredException(Throwable cause) {
        super(cause);
    }

    public PreconditionRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
