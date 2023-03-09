package com.jdcloud.gardener.fragrans.api.idempotent.support;

import com.jdcloud.gardener.fragrans.api.idempotent.core.IdempotentFactorStore;
import com.jdcloud.gardener.fragrans.data.cache.client.CacheOperator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2022/2/24 3:17 下午
 */
@AllArgsConstructor
public class CachedIdempotentFactorStore implements IdempotentFactorStore {
    private final CacheOperator<Integer> operator;

    private String composeFactorKey(HttpMethod method, String uri, String idempotentFactor) {
        return String.format("%s.%s.%s", method, uri, idempotentFactor);
    }

    @Override
    public boolean saveIfAbsent(HttpMethod method, String uri, String idempotentFactor, Duration ttl) {
        return operator.setIfNotPresents(composeFactorKey(method, uri, idempotentFactor), 1, ttl);
    }
}
