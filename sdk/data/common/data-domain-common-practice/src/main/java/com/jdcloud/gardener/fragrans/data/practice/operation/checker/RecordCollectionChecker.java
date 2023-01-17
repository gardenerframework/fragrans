package com.jdcloud.gardener.fragrans.data.practice.operation.checker;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/6/17 12:22 下午
 */
@FunctionalInterface
public interface RecordCollectionChecker<R> {
    /**
     * 检查记录集合
     * <p>
     * 默认可以不实现
     *
     * @param records 记录
     */
    <T extends R> void check(Collection<T> records);
}
