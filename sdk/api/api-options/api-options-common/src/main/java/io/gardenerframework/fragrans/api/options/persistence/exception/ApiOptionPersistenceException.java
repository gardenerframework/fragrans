package io.gardenerframework.fragrans.api.options.persistence.exception;

/**
 * @author zhanghan30
 * @date 2022/5/10 9:02 上午
 */
public abstract class ApiOptionPersistenceException extends RuntimeException {
    protected ApiOptionPersistenceException() {
    }

    public ApiOptionPersistenceException(String message) {
        super(message);
    }

    public ApiOptionPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiOptionPersistenceException(Throwable cause) {
        super(cause);
    }

    public ApiOptionPersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
