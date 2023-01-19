package io.gardenerframework.fragrans.api.standard.error.exception.client;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 依赖条件不满足
 *
 * @author zhanghan
 * @date 2020-11-12 20:00
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
@HttpStatusRepresentative
public class FailedDependencyException extends ApiStandardExceptions.ClientSideException {
    public FailedDependencyException() {
    }

    public FailedDependencyException(String message) {
        super(message);
    }

    public FailedDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedDependencyException(Throwable cause) {
        super(cause);
    }

    public FailedDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
