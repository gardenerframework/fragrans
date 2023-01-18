package io.gardenerframework.fragrans.data.cache.client;

import lombok.Setter;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanghan30
 * @date 2022/2/17 7:08 下午
 */
public class SpringRedisCacheClient implements RedisCacheClient {
    private final RedisTemplate<String, byte[]> redisTemplate;
    /**
     * 本地的脚本与redis hash的缓存
     */
    private final Map<String, String> scriptCache = new ConcurrentHashMap<>();
    /**
     * 是否自动重新加载脚本
     */
    @Setter
    private boolean autoReloadScript = true;

    public SpringRedisCacheClient(RedisConnectionFactory connectionFactory) {
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //透传字节流即可
        redisTemplate.setValueSerializer(new RedisSerializer<byte[]>() {
            @Nullable
            @Override
            public byte[] serialize(@Nullable byte[] bytes) throws SerializationException {
                return bytes;
            }

            @Nullable
            @Override
            public byte[] deserialize(@Nullable byte[] bytes) throws SerializationException {
                return bytes;
            }
        });
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
    }

    @Nullable
    @Override
    public byte[] get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(String key, byte[] content, @Nullable Duration ttl) {
        if (ttl == null) {
            redisTemplate.opsForValue().set(key, content);
        } else {
            redisTemplate.opsForValue().set(key, content, ttl);
        }
    }

    @Override
    public boolean setIfNotPresents(String key, byte[] content, @Nullable Duration ttl) {
        if (ttl == null) {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, content));
        } else {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, content, ttl));
        }
    }

    @Override
    public boolean setIfPresents(String key, byte[] content, @Nullable Duration ttl) {
        if (ttl == null) {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfPresent(key, content));
        } else {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfPresent(key, content, ttl));
        }
    }

    @Override
    public long increase(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public long decrease(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta * -1);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void updateTtl(String key, Duration ttl) {
        redisTemplate.expire(key, ttl);
    }

    @Nullable
    @Override
    public Duration ttl(String key) {
        Long expire = redisTemplate.getExpire(key);
        //按照redis的文档，-2代表key不存在
        if (Objects.equals(expire, -2L) || expire == null) {
            return null;
        } else if (Objects.equals(expire, -1L)) {
            //-1代表没有过期时间
            return Duration.ofMillis(Long.MAX_VALUE);
        }
        //返回正常的过期时间
        return Duration.ofSeconds(expire);
    }

    @Override
    public boolean supportLuaScript() {
        return true;
    }

    @Override
    public boolean scriptExists(String scriptHash) {
        return Boolean.TRUE.equals(this.redisTemplate.execute(
                new RedisCallback<Boolean>() {
                    @Nullable
                    @Override
                    public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                        List<Boolean> booleans = connection.scriptExists(scriptHash);
                        return !CollectionUtils.isEmpty(booleans) && booleans.get(0);
                    }
                }
        ));
    }

    @Override
    public String loadLuaScript(String content) {
        String hash = this.redisTemplate.execute(
                (RedisCallback<String>) connection -> connection.scriptingCommands().scriptLoad(
                        content.getBytes(StandardCharsets.UTF_8)
                )
        );
        //added for script auto reload
        this.scriptCache.putIfAbsent(hash, content);
        return hash;
    }

    @Nullable
    @Override
    public <T> T executeScript(String scriptHash, Class<T> resultType, int numberKeys, byte[]... keysAndArgs) {
        if (this.autoReloadScript && !scriptExists(scriptHash)) {
            //自动重新加载脚本
            this.loadLuaScript(this.scriptCache.get(scriptHash));
        }
        return this.redisTemplate.execute(
                (RedisCallback<T>) connection -> connection.scriptingCommands().evalSha(scriptHash.getBytes(StandardCharsets.UTF_8), ReturnType.fromJavaType(resultType), numberKeys, keysAndArgs)
        );
    }

    @Override
    public void hset(String key, String field, byte[] value) {
        this.redisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public boolean hsetnx(String key, String field, byte[] value) {
        return this.redisTemplate.opsForHash().putIfAbsent(key, field, value);
    }

    @Nullable
    @Override
    public byte[] hget(String key, String field) {
        return this.redisTemplate.<String, byte[]>opsForHash().get(key, field);
    }

    @Override
    public void hdelete(String key, String field) {
        this.redisTemplate.opsForHash().delete(key, field);
    }
}
