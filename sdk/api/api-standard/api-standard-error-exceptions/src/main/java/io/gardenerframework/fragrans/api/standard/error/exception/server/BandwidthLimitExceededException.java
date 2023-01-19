package io.gardenerframework.fragrans.api.standard.error.exception.server;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 带宽超过限制
 *
 * @author zhanghan
 * @date 2020-11-12 20:24
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
@HttpStatusRepresentative
public class BandwidthLimitExceededException extends ApiStandardExceptions.ServerSideException {
    public BandwidthLimitExceededException() {
    }

    public BandwidthLimitExceededException(String message) {
        super(message);
    }

    public BandwidthLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public BandwidthLimitExceededException(Throwable cause) {
        super(cause);
    }

    public BandwidthLimitExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
