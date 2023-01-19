package io.gardenerframework.fragrans.api.standard.error.exception.client;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 需要请求长度
 *
 * @author zhanghan
 * @date 2020-11-12 20:01
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.LENGTH_REQUIRED)
@HttpStatusRepresentative
public class LengthRequiredException extends ApiStandardExceptions.ClientSideException {
    public LengthRequiredException() {
    }

    public LengthRequiredException(String message) {
        super(message);
    }

    public LengthRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public LengthRequiredException(Throwable cause) {
        super(cause);
    }

    public LengthRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
