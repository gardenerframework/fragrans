package com.jdcloud.gardener.fragrans.api.standard.error.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/4/16 2:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiErrorWrappingException extends RuntimeException {
    /**
     * 包装的错误代码
     */
    private final transient String error;
    /**
     * 包装的状态码
     */
    private final transient HttpStatus status;
    /**
     * 包装的消息内容
     */
    private final transient String message;
    /**
     * 包装的详情
     */
    @Nullable
    private final transient Map<String, Object> details;

    public ApiErrorWrappingException(String error, HttpStatus status, String message) {
        this(error, status, message, null);
    }

    public ApiErrorWrappingException(String error, HttpStatus status, String message, @Nullable Map<String, Object> details) {
        super(message);
        this.error = error;
        this.status = status;
        this.message = message;
        this.details = details;
    }
}
