package io.gardenerframework.fragrans.data.cache.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.client.RedisCacheClient;
import io.gardenerframework.fragrans.data.cache.lock.CacheLock;
import io.gardenerframework.fragrans.data.cache.lock.context.LockContextHolder;
import io.gardenerframework.fragrans.data.cache.manager.log.schema.detail.DigestCacheDetail;
import io.gardenerframework.fragrans.data.cache.serialize.StringSerializer;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.common.schema.reason.NotFound;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Create;
import io.gardenerframework.fragrans.log.common.schema.verb.Process;
import io.gardenerframework.fragrans.log.common.schema.verb.Start;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 需要额外保证数据一致性
 *
 * @author ZhangHan
 * @date 2022/6/18 0:24
 */
@Slf4j
public abstract class DataConsistenceCacheManager<T> extends BasicCacheManager<T> {
    private final TaskScheduler taskScheduler;
    private final String setScript;
    private final String setNxScript;
    private final String setExScript;
    private final String updateTtlScript;
    private final String deleteScript;
    private final CacheLock cacheLock;
    private final StringSerializer stringSerializer = new StringSerializer();
    /**
     * 负责从数据源中读取源数据
     */
    private final Function<String, T> sourceReader;
    /**
     * 分布式锁的ttl，默认锁10秒
     */
    @Setter
    private Duration lockTll = Duration.ofSeconds(10);

    @Setter
    private Duration dataConsistenceTaskDelay = Duration.ofMinutes(1);

    protected DataConsistenceCacheManager(CacheClient cacheClient, LockContextHolder lockContextHolder, TaskScheduler taskScheduler, Function<String, T> sourceReader) throws IOException {
        this(cacheClient, lockContextHolder, null, taskScheduler, sourceReader);
    }

    protected DataConsistenceCacheManager(CacheClient cacheClient, LockContextHolder lockContextHolder, @Nullable ObjectMapper objectMapper, TaskScheduler taskScheduler, Function<String, T> sourceReader) throws IOException {
        this(cacheClient, lockContextHolder, objectMapper, null, taskScheduler, sourceReader);
    }

    protected DataConsistenceCacheManager(CacheClient cacheClient, LockContextHolder lockContextHolder, @Nullable ObjectMapper objectMapper, @Nullable Class<T> targetType, TaskScheduler taskScheduler, Function<String, T> sourceReader) throws IOException {
        super(cacheClient, objectMapper, targetType);
        this.taskScheduler = taskScheduler;
        this.sourceReader = sourceReader;
        if (supportLuaScript()) {
            RedisCacheClient redisCacheClient = (RedisCacheClient) this.getCacheClient();
            this.setScript = redisCacheClient.loadLuaScriptFile("data-cache-core/script/cache-manager/data-consistence-cache-manager/set.lua");
            this.setExScript = redisCacheClient.loadLuaScriptFile("data-cache-core/script/cache-manager/data-consistence-cache-manager/set-ex.lua");
            this.setNxScript = redisCacheClient.loadLuaScriptFile("data-cache-core/script/cache-manager/data-consistence-cache-manager/set-nx.lua");
            this.updateTtlScript = redisCacheClient.loadLuaScriptFile("data-cache-core/script/cache-manager/data-consistence-cache-manager/update-ttl.lua");
            this.deleteScript = redisCacheClient.loadLuaScriptFile("data-cache-core/script/cache-manager/data-consistence-cache-manager/delete.lua");
        } else {
            this.setScript = null;
            this.setNxScript = null;
            this.setExScript = null;
            this.deleteScript = null;
            this.updateTtlScript = null;
        }
        this.enableLogger(log);
        this.cacheLock = new CacheLock(cacheClient, lockContextHolder);
    }

    @Override
    public void set(@Nullable String[] namespaces, String id, @Nullable String suffix, T object, @Nullable Duration ttl) {
        String key = composeCacheKey(namespaces, id, suffix);
        String digestKey = composeDigestKey(key);
        String digest = Objects.requireNonNull(digestObject(object));
        this.cacheLock.lockThenRun(key, lockTll, new DataConsistenceTaskTrigger<>(
                () -> {
                    if (supportLuaScript()) {
                        executeScript(setScript,
                                2,
                                stringSerializer.serialize(key), stringSerializer.serialize(digestKey),
                                getSerializer().serialize(object), stringSerializer.serialize(digest),
                                ttl == null ? null : stringSerializer.serialize(String.valueOf(ttl.getSeconds()))
                        );
                    } else {
                        super.set(namespaces, id, suffix, object, ttl);
                        this.getCacheClient().set(digestKey, stringSerializer.serialize(digest), ttl);
                    }
                    writeCachedLog(log, new DigestCacheDetail(key, ttl, digestKey, digest));
                    return null;
                },
                namespaces, id, suffix));
    }

    @Override
    public boolean setIfPresents(@Nullable String[] namespaces, String id, @Nullable String suffix, T object, @Nullable Duration ttl) {
        String key = composeCacheKey(namespaces, id, suffix);
        String digestKey = composeDigestKey(key);
        String digest = Objects.requireNonNull(digestObject(object));
        return Boolean.TRUE.equals(this.cacheLock.lockThenRun(key, lockTll, new DataConsistenceTaskTrigger<>(
                () -> {
                    boolean done;
                    if (supportLuaScript()) {
                        done = isOk(executeScript(this.setExScript, 2,
                                stringSerializer.serialize(key), stringSerializer.serialize(digestKey),
                                getSerializer().serialize(object), stringSerializer.serialize(digest),
                                ttl == null ? null : stringSerializer.serialize(String.valueOf(ttl.getSeconds()))
                        ));
                    } else {
                        done = super.setIfPresents(namespaces, id, suffix, object, ttl);
                        if (done) {
                            //设置已经成功，直接覆盖摘要key(无需考虑是否存在)
                            this.getCacheClient().set(digestKey, stringSerializer.serialize(digest), ttl);
                        }
                    }
                    if (done) {
                        writeCachedLog(log, new DigestCacheDetail(key, ttl, digestKey, digest));
                    }
                    return done;
                },
                namespaces, id, suffix)));
    }

    @Override
    public boolean setIfNotPresents(@Nullable String[] namespaces, String id, @Nullable String suffix, T object, @Nullable Duration ttl) {
        String key = composeCacheKey(namespaces, id, suffix);
        String digestKey = composeDigestKey(key);
        String digest = Objects.requireNonNull(digestObject(object));
        return Boolean.TRUE.equals(this.cacheLock.lockThenRun(key, lockTll, new DataConsistenceTaskTrigger<>(
                () -> {
                    boolean done;
                    if (supportLuaScript()) {
                        done = isOk(executeScript(this.setNxScript, 2,
                                stringSerializer.serialize(key), stringSerializer.serialize(digestKey),
                                getSerializer().serialize(object), stringSerializer.serialize(digest),
                                ttl == null ? null : stringSerializer.serialize(String.valueOf(ttl.getSeconds()))
                        ));
                    } else {
                        done = super.setIfNotPresents(namespaces, id, suffix, object, ttl);
                        if (done) {
                            //设置已经成功，直接覆盖摘要key(无需考虑是否存在)
                            this.getCacheClient().set(digestKey, stringSerializer.serialize(digest), ttl);
                        }
                    }
                    if (done) {
                        writeCachedLog(log, new DigestCacheDetail(key, ttl, digestKey, digest));
                    }
                    return done;
                },
                namespaces, id, suffix)));
    }

    @Override
    public void delete(@Nullable String[] namespaces, String id, @Nullable String suffix) {
        String key = composeCacheKey(namespaces, id, suffix);
        String digestKey = composeDigestKey(key);
        this.cacheLock.lockThenRun(key, lockTll, () -> {
            if (supportLuaScript()) {
                executeScript(this.deleteScript, 2, stringSerializer.serialize(key), stringSerializer.serialize(digestKey));

            } else {
                super.delete(namespaces, id, suffix);
                this.getCacheClient().delete(digestKey);
            }
            writeCacheDeletedLog(log, new DigestCacheDetail(key, null, digestKey, null));
            return null;
        });
    }

    /**
     * 获取摘要
     *
     * @param id id
     * @return 摘要内容
     */
    @Nullable
    public String getDigest(String id) {
        return getDigest(scanNamespace(), id, scanSuffix());
    }

    /**
     * 获取摘要
     *
     * @param namespaces 命名空间
     * @param id         id
     * @param suffix     后缀
     * @return 摘要内容
     */
    @Nullable
    public String getDigest(@Nullable String[] namespaces, String id, @Nullable String suffix) {
        String key = composeCacheKey(namespaces, id, suffix);
        String digestKey = composeDigestKey(key);
        return stringSerializer.deserialize(getCacheClient().get(digestKey));
    }

    private boolean supportLuaScript() {
        return this.getCacheClient() instanceof RedisCacheClient && ((RedisCacheClient) this.getCacheClient()).supportLuaScript();
    }

    private String composeDigestKey(String cacheKey) {
        return String.format("%s.digest", cacheKey);
    }

    /**
     * 计算原始字节流
     *
     * @param rawData 原始字节流
     * @return 摘要
     */
    @Nullable
    public String digestRawData(@Nullable byte[] rawData) {
        if (rawData == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] digest = messageDigest.digest(rawData);
            BigInteger text = new BigInteger(1, digest);
            return text.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public void updateTtl(@Nullable String[] namespaces, String id, @Nullable String suffix, Duration ttl) {
        String key = composeCacheKey(namespaces, id, suffix);
        String digestKey = composeDigestKey(key);
        if (supportLuaScript()) {
            executeScript(this.updateTtlScript, 2, stringSerializer.serialize(key), stringSerializer.serialize(digestKey), stringSerializer.serialize(String.valueOf(ttl.getSeconds())));
        } else {
            super.updateTtl(namespaces, id, suffix, ttl);
            this.getCacheClient().updateTtl(digestKey, ttl);
        }
    }

    /**
     * 计算对象摘要
     *
     * @param object 对象
     * @return 摘要
     */
    @Nullable
    public String digestObject(@Nullable T object) {
        if (object == null) {
            return null;
        }
        return digestRawData(getSerializer().serialize(object));
    }

    /**
     * 执行脚本
     *
     * @param hash        脚本hash
     * @param numberKeys  key数量
     * @param keysAndArgs key和参数
     * @return 执行结果
     */
    private byte[] executeScript(String hash, int numberKeys, byte[]... keysAndArgs) {
        RedisCacheClient redisCacheClient = (RedisCacheClient) this.getCacheClient();
        Collection<byte[]> args = new ArrayList<>(keysAndArgs.length);
        for (byte[] item : keysAndArgs) {
            if (item != null) {
                args.add(item);
            }
        }
        return redisCacheClient.executeScript(hash, numberKeys, args.toArray(new byte[][]{}));
    }

    /**
     * 判断结果是不是"OK"
     *
     * @param scriptResult 脚本执行结果
     * @return 是否是"OK"
     */
    private boolean isOk(byte[] scriptResult) {
        if (scriptResult == null) {
            return false;
        }
        return "OK".equals(new String(scriptResult));
    }

    @AllArgsConstructor
    private class DataConsistenceTaskTrigger<R> implements Supplier<R> {
        private final Supplier<R> cacheOperation;
        private final String[] namespaces;
        private final String id;
        private final String suffix;

        @Override
        public R get() {
            R r = cacheOperation.get();
            String key = composeCacheKey(namespaces, id, suffix);
            String digestKey = composeDigestKey(key);
            //尝试锁定，启动延迟前只有一次调度
            if (cacheLock.tryLock(composeTaskKey(key), dataConsistenceTaskDelay) != null) {
                Instant eta = Instant.now().plus(dataConsistenceTaskDelay);
                taskScheduler.schedule(new DataConsistenceTask(namespaces, id, suffix), eta);
                GenericLoggerStaticAccessor.operationLogger().debug(
                        log,
                        GenericOperationLogContent.builder().what(DataConsistenceTask.class).operation(new Create()).state(new Done()).detail(
                                new DigestCacheDataConsistenceTaskDetail(key, null, digestKey, null, eta)
                        ).build(),
                        null
                );
            }
            return r;
        }

        private String composeTaskKey(String key) {
            return String.format("%s.data-consistence-task", key);
        }

        @AllArgsConstructor
        private class DataConsistenceTask implements Runnable {
            private final String[] namespaces;
            private final String id;
            private final String suffix;

            @Override
            public void run() {
                String key = composeCacheKey(namespaces, id, suffix);
                String digestKey = composeDigestKey(key);
                GenericLoggerStaticAccessor.operationLogger().debug(
                        log,
                        GenericOperationLogContent.builder().what(DataConsistenceTask.class).operation(new Process()).state(new Start()).detail(
                                new DigestCacheDetail(key, null, digestKey, null)
                        ).build(),
                        null
                );
                T source = sourceReader.apply(id);
                if (source == null) {
                    //原始数据已经不存在，删除缓存
                    GenericLoggerStaticAccessor.basicLogger().warn(
                            log,
                            GenericBasicLogContent.builder().what(getTargetType()).how(new NotFound()).detail(
                                    new Detail() {
                                        private String id;

                                        private Detail id(String id) {
                                            this.id = id;
                                            return this;
                                        }
                                    }.id(id)
                            ).build(),
                            null
                    );
                    delete(namespaces, id, suffix);
                }
                String digestFromDatabase = digestObject(source);
                String digest = stringSerializer.deserialize(getCacheClient().get(digestKey));
                if (!Objects.equals(digestFromDatabase, digest)) {
                    GenericLoggerStaticAccessor.basicLogger().warn(
                            log,
                            GenericBasicLogContent.builder().what(getTargetType()).how(new SourceUpdated()).detail(
                                    new Detail() {
                                        private String id;
                                        private String cachedDigest = digest;
                                        private String sourceDigest = digestFromDatabase;

                                        private Detail id(String id) {
                                            this.id = id;
                                            return this;
                                        }
                                    }.id(id)
                            ).build(),
                            null
                    );
                    delete(namespaces, id, suffix);
                }
                GenericLoggerStaticAccessor.operationLogger().debug(
                        log,
                        GenericOperationLogContent.builder().what(DataConsistenceTask.class).operation(new Process()).state(new Done()).detail(
                                new DigestCacheDetail(key, null, digestKey, null)
                        ).build(),
                        null
                );
            }

            private class SourceUpdated implements Word {
                @Override
                public String toString() {
                    return "数据源已发生变化";
                }
            }
        }

        private class DigestCacheDataConsistenceTaskDetail extends DigestCacheDetail {
            private final String eta;

            public DigestCacheDataConsistenceTaskDetail(String key, @Nullable Duration ttl, String digestKey, String digest, Instant eta) {
                super(key, ttl, digestKey, digest);
                this.eta = new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601).format(Date.from(eta));
            }
        }
    }

}
