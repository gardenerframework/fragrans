package io.gardenerframework.fragrans.api.standard.error.exception.client;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiStandardExceptions;
import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 因法律问题不可用
 *
 * @author zhanghan
 * @date 2020-11-12 20:20
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
@HttpStatusRepresentative
public class UnavailableForLegalReasonsException extends ApiStandardExceptions.ClientSideException {
    public UnavailableForLegalReasonsException() {
    }

    public UnavailableForLegalReasonsException(String message) {
        super(message);
    }

    public UnavailableForLegalReasonsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableForLegalReasonsException(Throwable cause) {
        super(cause);
    }

    public UnavailableForLegalReasonsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
