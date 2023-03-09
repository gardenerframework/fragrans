package io.gardenerframework.fragrans.api.standard.error.exception.client;

/**
 * 请求参数有问题
 *
 * @author ZhangHan
 * @date 2021/8/20 1:06
 */
public class BadRequestArgumentException extends BadRequestException {
    public BadRequestArgumentException() {
    }

    public BadRequestArgumentException(String message) {
        super(message);
    }

    public BadRequestArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestArgumentException(Throwable cause) {
        super(cause);
    }

    public BadRequestArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
