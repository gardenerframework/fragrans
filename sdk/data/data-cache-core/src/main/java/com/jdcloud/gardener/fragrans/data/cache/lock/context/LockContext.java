package com.jdcloud.gardener.fragrans.data.cache.lock.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

/**
 * @author zhanghan30
 * @date 2022/6/22 3:12 下午
 */
@AllArgsConstructor
@Getter
public class LockContext {
    /**
     * 当前锁是重入的，也就是有外层已经锁定
     */
    private final boolean reentered;
    /**
     * 锁的过期时间
     * <p>
     * 如果是重入的，则是外层锁的过期时间
     */
    private final Date expiresAt;
}
