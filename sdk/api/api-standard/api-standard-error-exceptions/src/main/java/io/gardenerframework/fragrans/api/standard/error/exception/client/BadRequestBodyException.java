package io.gardenerframework.fragrans.api.standard.error.exception.client;

/**
 * 请求提有问题
 *
 * @author ZhangHan
 * @date 2021/8/20 1:06
 */
public class BadRequestBodyException extends BadRequestException {
    public BadRequestBodyException() {
    }

    public BadRequestBodyException(String message) {
        super(message);
    }

    public BadRequestBodyException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestBodyException(Throwable cause) {
        super(cause);
    }

    public BadRequestBodyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
