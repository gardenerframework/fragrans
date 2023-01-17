package com.jdcloud.gardener.fragrans.data.cache.lock;

import com.jdcloud.gardener.fragrans.data.cache.client.CacheClient;
import com.jdcloud.gardener.fragrans.data.cache.client.RedisCacheClient;
import com.jdcloud.gardener.fragrans.data.cache.lock.context.LockContext;
import com.jdcloud.gardener.fragrans.data.cache.lock.context.LockContextHolder;
import com.jdcloud.gardener.fragrans.data.cache.lock.log.schema.detail.CacheLockDetail;
import com.jdcloud.gardener.fragrans.data.cache.serialize.StringSerializer;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Lock;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Release;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.Supplier;

/**
 * 获取缓存锁
 *
 * @author zhanghan30
 * @date 2022/2/14 12:01 下午
 */
@LogTarget("缓存锁")
@Slf4j
public class CacheLock {
    private final CacheClient cacheClient;
    private final StringSerializer serializer = new StringSerializer();
    private final String releaseLockScript;
    private final LockContextHolder lockContextHolder;

    public CacheLock(CacheClient cacheClient, LockContextHolder lockContextHolder) throws IOException {
        this.cacheClient = cacheClient;
        this.lockContextHolder = lockContextHolder;
        if (supportLuaScript()) {
            this.releaseLockScript = ((RedisCacheClient) this.cacheClient).loadLuaScriptFile("data-cache-core/script/cache-lock/release-lock.lua");
        } else {
            this.releaseLockScript = null;
        }

    }

    /**
     * 写一下当前是谁在锁定
     * <p>
     * 构成是当前进程id.当前线程名称@当前主机ip
     *
     * @return locker的名称
     */
    protected String composeLockerName() {
        try {
            //ip地址
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            //进程id
            String processId = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            //线程id
            String threadId = Thread.currentThread().getName();
            return String.format("%s.%s@%s", processId, threadId, hostAddress);
        } catch (UnknownHostException exception) {
            throw new UnsupportedOperationException(exception);
        }
    }

    /**
     * 生成锁的key
     *
     * @param key key
     * @return 锁的key
     */
    private String composeLockKey(String key) {
        return String.format("%s.lock", key);
    }

    /**
     * 尝试获得锁
     *
     * @param key key
     * @param ttl 锁多长时间
     * @return 锁上下文，为null的意思是没有锁定上
     */
    @Nullable
    public LockContext tryLock(String key, Duration ttl) {
        Assert.notNull(ttl, "tll must not be null");
        String lockerName = composeLockerName();
        String lockKey = composeLockKey(key);
        LockContext context = lockContextHolder.get(lockKey);
        //获取上下文的过期时间，有可能是第一次锁定，所以压根没有上下文
        Date expiresAt = context == null ? null : context.getExpiresAt();
        //查看当前线程是否已经有外部方法上锁且锁的过期时间还没有到
        if (expiresAt != null && new Date().before(expiresAt)) {
            GenericLoggerStaticAccessor.operationLogger()
                    .debug(log,
                            GenericOperationLogContent
                                    .builder()
                                    .what(CacheLock.class)
                                    .operation(new Lock()).state(new Done())
                                    .detail(new CacheLockDetail(lockKey, Duration.between(Instant.now(), expiresAt.toInstant()), lockerName, true))
                                    .build(), null
                    );
            return new LockContext(true, expiresAt);
        }
        boolean locked = this.cacheClient.setIfNotPresents(lockKey, serializer.serialize(lockerName), ttl);
        if (locked) {
            //成功锁定，更新线程记录的锁过期时间
            LockContext lockContext = new LockContext(false, Date.from(Instant.now().plus(ttl)));
            //设置一个非重入的上下文
            lockContextHolder.set(lockKey, lockContext);
            GenericLoggerStaticAccessor.operationLogger()
                    .debug(log,
                            GenericOperationLogContent.builder()
                                    .what(CacheLock.class)
                                    .operation(new Lock()).state(new Done())
                                    .detail(new CacheLockDetail(lockKey, ttl, lockerName, false))
                                    .build(), null
                    );
            return lockContext;
        }
        return null;
    }

    /**
     * 获取锁
     *
     * @param key key
     * @param ttl 锁多长时间
     */
    public LockContext lock(String key, Duration ttl) {
        LockContext context;
        while ((context = tryLock(key, ttl)) == null) {
            try {
                //释放cpu 1毫秒
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        return context;
    }

    /**
     * 释放锁
     *
     * @param key key
     */
    public void releaseLock(String key, LockContext context) {
        Assert.notNull(context, "context must not be null");
        String lockKey = composeLockKey(key);
        String lockerName = composeLockerName();
        if (!context.isReentered()) {
            if (supportLuaScript()) {
                ((RedisCacheClient) this.cacheClient).executeScript(this.releaseLockScript, 1, serializer.serialize(lockKey), serializer.serialize(lockerName));
            } else {
                String lockerNameFromCache = serializer.deserialize(this.cacheClient.get(composeLockKey(key)));
                if (StringUtils.hasText(lockerNameFromCache) && lockerNameFromCache.equals(lockerName)) {
                    //只释放由当前线程锁定的锁
                    //其实这样并不是非常安全
                    this.cacheClient.delete(lockKey);
                }
            }
            lockContextHolder.remove(lockKey);
        }
        GenericLoggerStaticAccessor.operationLogger()
                .debug(
                        log,
                        GenericOperationLogContent.builder().what(CacheLock.class)
                                .operation(new Release())
                                .state(new Done())
                                .detail(new CacheLockDetail(lockKey, Duration.between(Instant.now(), context.getExpiresAt().toInstant()), lockerName, context.isReentered())).build(),
                        null
                );
    }

    /**
     * 获得锁后运行拉姆达表达式
     *
     * @param key      key
     * @param ttl      锁多长时间
     * @param callback 表达式
     */
    @Nullable
    public <T> T lockThenRun(String key, Duration ttl, Supplier<T> callback) {
        return lockThenRun(key, ttl, true, callback);
    }

    /**
     * 获得锁后运行拉姆达表达式
     *
     * @param key      key
     * @param ttl      锁多长时间
     * @param callback 表达式
     */
    @Nullable
    public <T> T lockThenRun(String key, Duration ttl, boolean autoRelease, Supplier<T> callback) {
        LockContext context = null;
        try {
            context = lock(key, ttl);
            return callback.get();
        } finally {
            if (autoRelease && context != null) {
                //如果context == null。那说明中间遇到了异常中断了
                releaseLock(key, context);
            }
        }
    }

    private boolean supportLuaScript() {
        return this.cacheClient instanceof RedisCacheClient && ((RedisCacheClient) this.cacheClient).supportLuaScript();
    }
}
