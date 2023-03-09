package io.gardenerframework.fragrans.api.options.exception.client;

import io.gardenerframework.fragrans.api.standard.error.exception.client.ConflictException;

/**
 * @author zhanghan30
 * @date 2022/5/10 9:08 上午
 */
public class ApiOptionVersionNumberConflictException extends ConflictException {
    public ApiOptionVersionNumberConflictException(Throwable cause) {
        super(cause);
    }
}
