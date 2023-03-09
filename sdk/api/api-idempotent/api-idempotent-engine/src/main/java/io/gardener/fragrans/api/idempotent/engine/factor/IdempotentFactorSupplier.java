package com.jdcloud.gardener.fragrans.api.idempotent.engine.factor;

import org.springframework.http.HttpMessage;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * 从http请求中提取
 *
 * @author zhanghan30
 * @date 2022/2/24 2:13 下午
 */
@FunctionalInterface
public interface IdempotentFactorSupplier {
    /**
     * 从http请求中获得幂等因子
     *
     * @param request http 请求
     * @return 幂等因子
     */
    @Nullable
    String getIdempotentFactor(HttpServletRequest request);
}
