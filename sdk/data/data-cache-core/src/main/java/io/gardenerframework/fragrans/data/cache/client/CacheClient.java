package io.gardenerframework.fragrans.data.cache.client;

import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * 缓存客户端接口，提供缓存最基本的，也是业务无关的操作
 *
 * @author zhanghan30
 * @date 2022/2/12 10:50 上午
 */
public interface CacheClient {
    /**
     * 获取缓存数据
     *
     * @param key 缓存key
     * @return 缓存数据
     */
    @Nullable
    byte[] get(String key);

    /**
     * 设置缓存
     *
     * @param key     缓存key
     * @param content 缓存内容
     * @param ttl     缓存时间
     */
    void set(String key, byte[] content, @Nullable Duration ttl);

    /**
     * 设置不过期的缓存
     *
     * @param key     缓存key
     * @param content 缓存内容
     */
    default void set(String key, byte[] content) {
        set(key, content, null);
    }

    /**
     * 当不存在时设置缓存
     *
     * @param key     缓存key
     * @param content 缓存内容
     * @param ttl     缓存周期
     */
    boolean setIfNotPresents(String key, byte[] content, @Nullable Duration ttl);

    /**
     * 当不存在时设置不过期的缓存
     *
     * @param key     缓存key
     * @param content 缓存内容
     * @return 是否设置成功
     */
    default boolean setIfNotPresents(String key, byte[] content) {
        return setIfNotPresents(key, content, null);
    }

    /**
     * 当存在时设置缓存
     *
     * @param key     缓存key
     * @param content 缓存内容
     * @param ttl     缓存周期
     */
    boolean setIfPresents(String key, byte[] content, @Nullable Duration ttl);

    /**
     * 当存在时设置不过期的缓存
     *
     * @param key     缓存key
     * @param content 缓存内容
     * @return 是否设置成功
     */
    default boolean setIfPresents(String key, byte[] content) {
        return setIfPresents(key, content, null);
    }

    /**
     * 增加
     *
     * @param key   缓存key
     * @param delta 变化量
     * @return 变化后的数值
     */
    long increase(String key, long delta);

    /**
     * 减少
     *
     * @param key   缓存key
     * @param delta 变化量
     * @return 变化后的数值
     */
    long decrease(String key, long delta);

    /**
     * 删除缓存
     *
     * @param key 缓存key
     */
    void delete(String key);

    /**
     * 更新缓存时间
     *
     * @param key 缓存key
     * @param ttl 新的缓存时间
     */
    void updateTtl(String key, Duration ttl);

    /**
     * 获得指定key的ttl
     *
     * @param key 缓存key
     * @return ttl
     */
    @Nullable
    Duration ttl(String key);
}
