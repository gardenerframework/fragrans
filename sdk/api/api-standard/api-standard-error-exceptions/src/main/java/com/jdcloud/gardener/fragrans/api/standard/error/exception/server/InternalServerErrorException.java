package com.jdcloud.gardener.fragrans.api.standard.error.exception.server;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 服务内部异常
 *
 * @author zhanghan
 * @date 2020-11-12 20:27
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@HttpStatusRepresentative
public class InternalServerErrorException extends ApiStandardExceptions.ServerSideException {
    public InternalServerErrorException() {
    }

    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }

    public InternalServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
