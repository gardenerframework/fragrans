package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 代理服务需要提供认证信息
 *
 * @author zhanghan
 * @date 2020-11-12 20:12
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.PROXY_AUTHENTICATION_REQUIRED)
@HttpStatusRepresentative
public class ProxyAuthenticationRequiredException extends ApiStandardExceptions.ClientSideException {
    public ProxyAuthenticationRequiredException() {
    }

    public ProxyAuthenticationRequiredException(String message) {
        super(message);
    }

    public ProxyAuthenticationRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyAuthenticationRequiredException(Throwable cause) {
        super(cause);
    }

    public ProxyAuthenticationRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
