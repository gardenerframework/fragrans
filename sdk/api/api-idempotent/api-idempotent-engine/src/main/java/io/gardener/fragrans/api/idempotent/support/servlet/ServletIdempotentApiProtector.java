package com.jdcloud.gardener.fragrans.api.idempotent.support.servlet;

import com.jdcloud.gardener.fragrans.api.idempotent.core.AbstractIdempotentApiProtector;
import com.jdcloud.gardener.fragrans.api.idempotent.core.IdempotentFactorStore;
import com.jdcloud.gardener.fragrans.api.idempotent.engine.factor.IdempotentFactorSupplier;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2022/2/24 3:59 下午
 */
@Getter
@Setter
public class ServletIdempotentApiProtector extends AbstractIdempotentApiProtector {
    private Duration ttl = Duration.ofMinutes(2);

    public ServletIdempotentApiProtector(IdempotentFactorSupplier idempotentFactorSupplier, IdempotentFactorStore idempotentFactorStore) {
        super(idempotentFactorSupplier, idempotentFactorStore);
    }

    @Override
    protected HttpRequest getHttpRequest() {
        return new ServletServerHttpRequest(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
    }

    @Override
    protected Duration getIdempotentFactorTtl(HttpRequest request) {
        return ttl;
    }
}
