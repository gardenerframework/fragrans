package com.jdcloud.gardener.fragrans.api.idempotent.support;

import com.jdcloud.gardener.fragrans.api.idempotent.engine.factor.IdempotentFactorSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 通过在http 头中x-request-nounce获得幂等因子
 *
 * @author zhanghan30
 * @date 2022/2/24 2:25 下午
 */
public class RequestNounceIdempotentFactorSupplier implements IdempotentFactorSupplier {
    public static final String X_REQUEST_NOUNCE = "x-request-nounce";

    @Override
    @Nullable
    public String getIdempotentFactor(HttpRequest request) {
        Assert.notNull(request, "request must not be null");
        HttpHeaders httpHeaders = request.getHeaders();
        return httpHeaders == null ? null : httpHeaders.getFirst(X_REQUEST_NOUNCE);
    }
}
