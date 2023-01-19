package io.gardenerframework.fragrans.api.standard.error.exception.server;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 网关连接后端服务超时
 *
 * @author zhanghan
 * @date 2020-11-12 20:26
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
@HttpStatusRepresentative
public class GatewayTimeoutException extends ApiStandardExceptions.ServerSideException {
    public GatewayTimeoutException() {
    }

    public GatewayTimeoutException(String message) {
        super(message);
    }

    public GatewayTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayTimeoutException(Throwable cause) {
        super(cause);
    }

    public GatewayTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
