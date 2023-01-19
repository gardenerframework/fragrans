package io.gardenerframework.fragrans.api.standard.error.exception.client;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 权限不足，禁止访问
 *
 * @author zhanghan
 * @date 2020-11-12 20:00
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
@HttpStatusRepresentative
public class ForbiddenException extends ApiStandardExceptions.ClientSideException {
    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }

    public ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
