package io.gardenerframework.fragrans.api.standard.error.exception.server;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 服务当前不可用，也许一会儿后能恢复
 *
 * @author zhanghan
 * @date 2020-11-12 20:29
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
@HttpStatusRepresentative
public class ServiceUnavailableException extends ApiStandardExceptions.ServerSideException {
    public ServiceUnavailableException() {
    }

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceUnavailableException(Throwable cause) {
        super(cause);
    }

    public ServiceUnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
