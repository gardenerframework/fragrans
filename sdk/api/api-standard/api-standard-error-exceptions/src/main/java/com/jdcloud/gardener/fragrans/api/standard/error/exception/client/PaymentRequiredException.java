package com.jdcloud.gardener.fragrans.api.standard.error.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiStandardExceptions;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 需付费才能使用
 *
 * @author zhanghan
 * @date 2020-11-12 20:08
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
@HttpStatusRepresentative
public class PaymentRequiredException extends ApiStandardExceptions.ClientSideException {
    public PaymentRequiredException() {
    }

    public PaymentRequiredException(String message) {
        super(message);
    }

    public PaymentRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentRequiredException(Throwable cause) {
        super(cause);
    }

    public PaymentRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
