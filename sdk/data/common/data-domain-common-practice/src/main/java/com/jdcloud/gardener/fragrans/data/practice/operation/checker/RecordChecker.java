package com.jdcloud.gardener.fragrans.data.practice.operation.checker;

import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/6/17 12:22 下午
 */
@FunctionalInterface
public interface RecordChecker<R> {
    /**
     * 检查单个记录
     *
     * @param record 记录
     */
    <T extends R> void check(@Nullable T record);
}
