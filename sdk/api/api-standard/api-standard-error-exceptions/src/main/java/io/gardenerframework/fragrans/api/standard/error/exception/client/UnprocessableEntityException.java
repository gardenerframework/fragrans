package io.gardenerframework.fragrans.api.standard.error.exception.client;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 无法处理请求体
 *
 * @author zhanghan
 * @date 2020-11-12 20:21
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@HttpStatusRepresentative
public class UnprocessableEntityException extends ApiStandardExceptions.ClientSideException {
    public UnprocessableEntityException() {
    }

    public UnprocessableEntityException(String message) {
        super(message);
    }

    public UnprocessableEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnprocessableEntityException(Throwable cause) {
        super(cause);
    }

    public UnprocessableEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
