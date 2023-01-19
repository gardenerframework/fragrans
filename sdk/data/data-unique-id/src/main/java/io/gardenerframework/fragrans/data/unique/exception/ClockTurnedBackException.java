package io.gardenerframework.fragrans.data.unique.exception;

import io.gardenerframework.fragrans.data.unique.UniqueIdGenerator;

/**
 * 时间回退异常，用于雪花id生成算法
 *
 * @author zhanghan
 * @date 2021/8/19 14:50
 * @see UniqueIdGenerator
 * @since 1.0.0
 */
public class ClockTurnedBackException extends RuntimeException {

    public ClockTurnedBackException(String message) {
        super(message);
    }
}
