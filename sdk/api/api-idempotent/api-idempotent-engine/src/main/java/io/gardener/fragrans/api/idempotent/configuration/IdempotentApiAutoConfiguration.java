package com.jdcloud.gardener.fragrans.api.idempotent.configuration;

import com.jdcloud.gardener.fragrans.api.standard.error.configuration.DomainErrorPackage;
import com.jdcloud.gardener.fragrans.api.idempotent.core.IdempotentFactorStore;
import com.jdcloud.gardener.fragrans.api.idempotent.engine.factor.IdempotentFactorSupplier;
import com.jdcloud.gardener.fragrans.api.idempotent.exception.DuplicateHttpRequestException;
import com.jdcloud.gardener.fragrans.api.idempotent.support.CachedIdempotentFactorStore;
import com.jdcloud.gardener.fragrans.api.idempotent.support.RequestNounceIdempotentFactorSupplier;
import com.jdcloud.gardener.fragrans.api.idempotent.support.servlet.ServletIdempotentApiProtector;
import com.jdcloud.gardener.fragrans.data.cache.client.CacheClient;
import com.jdcloud.gardener.fragrans.data.cache.serialize.IntegerSerializer;
import com.jdcloud.gardener.fragrans.data.cache.client.CacheOperator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/2/24 3:54 下午
 */
@Configuration
@DomainErrorPackage(baseClasses = DuplicateHttpRequestException.class)
public class IdempotentApiAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(IdempotentFactorStore.class)
    public IdempotentFactorStore cachedIdempotentFactorStore(CacheClient cacheClient) {
        return new CachedIdempotentFactorStore(new CacheOperator<>(cacheClient, new IntegerSerializer()));
    }

    @Bean
    @ConditionalOnMissingBean(IdempotentFactorSupplier.class)
    public IdempotentFactorSupplier requestNounceIdempotentFactorSupplier() {
        return new RequestNounceIdempotentFactorSupplier();
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public ServletIdempotentApiProtector servletIdempotentApiProtector(
            IdempotentFactorSupplier idempotentFactorSupplier,
            IdempotentFactorStore idempotentFactorStore
    ) {
        return new ServletIdempotentApiProtector(idempotentFactorSupplier, idempotentFactorStore);
    }
}
