package io.gardenerframework.fragrans.api.standard.error.exception.client;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 请求超时，这是由于客户端连接后很长时间没有发送数据导致的
 *
 * @author zhanghan
 * @date 2020-11-12 20:14
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
@HttpStatusRepresentative
public class RequestTimeoutException extends ApiStandardExceptions.ClientSideException {
    public RequestTimeoutException() {
    }

    public RequestTimeoutException(String message) {
        super(message);
    }

    public RequestTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestTimeoutException(Throwable cause) {
        super(cause);
    }

    public RequestTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
