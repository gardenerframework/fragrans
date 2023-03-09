package io.gardenerframework.fragrans.data.cache.lock.log.schema.detail;

import io.gardenerframework.fragrans.data.cache.log.schema.detail.CacheDetail;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author ZhangHan
 * @date 2022/6/17 23:16
 */
public class CacheLockDetail extends CacheDetail {
    private final String locker;
    private final boolean reentered;

    public CacheLockDetail(String key, @Nullable Duration ttl, String locker, boolean reentered) {
        super(key, ttl);
        this.locker = locker;
        this.reentered = reentered;
    }
}
