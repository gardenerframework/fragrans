package com.jdcloud.gardener.fragrans.api.options.persistence.exception;

/**
 * 版本号已过期异常
 *
 * @author zhanghan30
 * @date 2022/5/10 6:43 上午
 */
public class ApiOptionVersionOutOfDateException extends ApiOptionPersistenceException {
    public ApiOptionVersionOutOfDateException(String id, String currentVersion) {
        super("id: " + id + ", version: " + currentVersion);
    }
}
