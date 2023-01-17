package com.jdcloud.gardener.fragrans.data.cache.manager.log.schema.detail;

import com.jdcloud.gardener.fragrans.data.cache.log.schema.detail.CacheDetail;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author ZhangHan
 * @date 2022/6/18 1:04
 */
public class DigestCacheDetail extends CacheDetail {
    private final String degistKey;
    private final String deigest;

    public DigestCacheDetail(String key, @Nullable Duration ttl, String digestKey, String digest) {
        super(key, ttl);
        this.degistKey = digestKey;
        this.deigest = digest;
    }
}
