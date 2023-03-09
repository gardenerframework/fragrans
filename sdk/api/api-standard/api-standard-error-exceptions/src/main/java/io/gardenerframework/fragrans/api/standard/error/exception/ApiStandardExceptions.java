package io.gardenerframework.fragrans.api.standard.error.exception;

/**
 * @author zhanghan30
 * @date 2022/8/26 1:15 下午
 */
public interface ApiStandardExceptions {
    /**
     * api标准异常
     */
    class ApiStandardException extends RuntimeException {
        public ApiStandardException() {
        }

        public ApiStandardException(String message) {
            super(message);
        }

        public ApiStandardException(String message, Throwable cause) {
            super(message, cause);
        }

        public ApiStandardException(Throwable cause) {
            super(cause);
        }

        public ApiStandardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    /**
     * 调用方问题
     */
    class ClientSideException extends ApiStandardException {
        public ClientSideException() {
        }

        public ClientSideException(String message) {
            super(message);
        }

        public ClientSideException(String message, Throwable cause) {
            super(message, cause);
        }

        public ClientSideException(Throwable cause) {
            super(cause);
        }

        public ClientSideException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    /**
     * 服务方问题
     */
    class ServerSideException extends ApiStandardException {
        public ServerSideException() {
        }

        public ServerSideException(String message) {
            super(message);
        }

        public ServerSideException(String message, Throwable cause) {
            super(message, cause);
        }

        public ServerSideException(Throwable cause) {
            super(cause);
        }

        public ServerSideException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
