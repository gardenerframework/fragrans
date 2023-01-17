package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 升级请求协议
 *
 * @author zhanghan
 * @date 2020-11-12 20:21
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.UPGRADE_REQUIRED)
@HttpStatusRepresentative
public class UpgradeRequiredException extends ApiStandardExceptions.ClientSideException {
    public UpgradeRequiredException() {
    }

    public UpgradeRequiredException(String message) {
        super(message);
    }

    public UpgradeRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpgradeRequiredException(Throwable cause) {
        super(cause);
    }

    public UpgradeRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
