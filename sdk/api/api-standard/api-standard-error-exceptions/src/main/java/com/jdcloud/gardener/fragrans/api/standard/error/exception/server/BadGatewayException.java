package com.jdcloud.gardener.fragrans.api.standard.error.exception.server;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 网关无法连接后端
 *
 * @author zhanghan
 * @date 2020-11-12 19:00
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
@HttpStatusRepresentative
public class BadGatewayException extends ApiStandardExceptions.ServerSideException {
    public BadGatewayException() {
    }

    public BadGatewayException(String message) {
        super(message);
    }

    public BadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadGatewayException(Throwable cause) {
        super(cause);
    }

    public BadGatewayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
