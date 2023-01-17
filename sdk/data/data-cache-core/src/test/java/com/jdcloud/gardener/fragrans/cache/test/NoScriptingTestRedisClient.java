package com.jdcloud.gardener.fragrans.cache.test;

import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author zhanghan30
 * @date 2022/3/31 2:52 下午
 */
public class NoScriptingTestRedisClient extends TestRedisClient {

    public NoScriptingTestRedisClient(RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    public boolean supportLuaScript() {
        return false;
    }
}
