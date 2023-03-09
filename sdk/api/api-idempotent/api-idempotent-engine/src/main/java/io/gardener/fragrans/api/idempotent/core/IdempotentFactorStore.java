package com.jdcloud.gardener.fragrans.api.idempotent.core;

import org.springframework.http.HttpMethod;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2022/2/24 3:03 下午
 */
@FunctionalInterface
public interface IdempotentFactorStore {
    /**
     * 获得给定的幂等因子
     *
     * @param method           http方法
     * @param uri              请求地址
     * @param idempotentFactor 因子
     * @param ttl              有效期
     * @return 是否保存成功
     */
    boolean saveIfAbsent(HttpMethod method, String uri, String idempotentFactor, Duration ttl);
}
