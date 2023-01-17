package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 未找到资源或资源不存在
 *
 * @author zhanghan
 * @date 2020-11-12 20:05
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@HttpStatusRepresentative
public class NotFoundException extends ApiStandardExceptions.ClientSideException {
    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
