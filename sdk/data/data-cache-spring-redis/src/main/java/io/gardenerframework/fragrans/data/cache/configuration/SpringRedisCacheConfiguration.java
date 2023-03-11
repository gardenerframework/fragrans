package io.gardenerframework.fragrans.data.cache.configuration;

import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.client.SpringRedisCacheClient;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * {@link AutoConfigureOrder}是有必要的注解，如果其它依赖类没有注解， 不会因为加载顺序的问题导致无法找到bean
 * <p>
 * 毕竟S开头的字母排序太靠后
 *
 * @author zhanghan30
 * @date 2022/2/17 7:26 下午
 */
@Configuration
@ConditionalOnClass(RedisConnectionFactory.class)
@AutoConfigureOrder(-1)
@ConditionalOnMissingBean(CacheClient.class)
@ComponentScan(
        basePackageClasses = SpringRedisCacheClient.class,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SpringRedisCacheClient.class
                )
        }
)
public class SpringRedisCacheConfiguration {
}
