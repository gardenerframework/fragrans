package com.jdcloud.gardener.fragrans.api.standard.error.exception.server;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 功能未实现，无法完成请求
 *
 * @author zhanghan
 * @date 2020-11-12 20:28
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
@HttpStatusRepresentative
public class NotImplementedException extends ApiStandardExceptions.ServerSideException {
    public NotImplementedException() {
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }

    public NotImplementedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
