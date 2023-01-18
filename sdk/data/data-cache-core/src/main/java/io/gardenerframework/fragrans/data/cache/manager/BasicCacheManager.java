package io.gardenerframework.fragrans.data.cache.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.log.schema.detail.CacheDetail;
import io.gardenerframework.fragrans.data.cache.manager.annotation.Cached;
import io.gardenerframework.fragrans.data.cache.serialize.*;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.GenericOperationLoggerMethodTemplate;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Cache;
import io.gardenerframework.fragrans.log.common.schema.verb.Delete;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.data.cache.serialize.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 抽象的缓存管理器
 *
 * @author ZhangHan
 * @date 2021/9/28 0:39
 */
@Slf4j
public abstract class BasicCacheManager<T> {
    /**
     * 缓存客户端
     */
    @Getter(AccessLevel.PROTECTED)
    private final CacheClient cacheClient;
    /**
     * 序列化器
     */
    @Getter(AccessLevel.PROTECTED)
    private final Serializer<T> serializer;
    /**
     * 目标类型
     */
    @Getter(AccessLevel.PROTECTED)
    private final Class<T> targetType;
    /**
     * 日志记录模板
     */
    @Setter(AccessLevel.PROTECTED)
    private GenericOperationLoggerMethodTemplate loggingMethod;
    /**
     * 哪个日志记录器生效
     */
    private Logger enabledLogger = BasicCacheManager.log;

    protected BasicCacheManager(CacheClient cacheClient) {
        this(cacheClient, null, null);
    }

    protected BasicCacheManager(CacheClient cacheClient, @Nullable ObjectMapper objectMapper) {
        this(cacheClient, objectMapper, null);
    }


    protected BasicCacheManager(CacheClient cacheClient, @Nullable ObjectMapper objectMapper, @Nullable Class<T> targetType) {
        this.cacheClient = cacheClient;
        this.targetType = targetType == null ? getSubclassParameterizedType() : targetType;
        this.serializer = deduceSerializer(objectMapper);
        loggingMethod = GenericLoggerStaticAccessor.operationLogger()::debug;
    }

    /**
     * 获取缓存
     *
     * @param id 对象id
     * @return 缓存
     */
    public T get(String id) {
        return get(scanNamespace(), id, scanSuffix());
    }

    /**
     * 获取缓存
     * <p>
     * 这种一般是用于常规的类型的缓存(不怎么常用就是了)，比如一个字符串的缓存
     *
     * @param id 对象id
     * @return 缓存
     */
    @Nullable
    public T get(@Nullable String[] namespaces, String id, @Nullable String suffix) {
        return serializer.deserialize(this.cacheClient.get(composeCacheKey(namespaces, id, suffix)));
    }


    /**
     * 设置不需要摘要的缓存
     *
     * @param id     对象id
     * @param object 对象
     * @param ttl    缓存时间
     */
    public void set(String id, T object, @Nullable Duration ttl) {
        set(scanNamespace(), id, scanSuffix(), object, ttl);
    }


    /**
     * 覆盖一个已有的缓存
     *
     * @param namespaces 命名空间
     * @param id         缓存id
     * @param suffix     后缀
     * @param object     缓存对象
     * @param ttl        缓存时间
     */
    public void set(@Nullable String[] namespaces, String id, @Nullable String suffix, T object, @Nullable Duration ttl) {
        String cacheKey = composeCacheKey(namespaces, id, suffix);
        this.cacheClient.set(
                cacheKey,
                serializer.serialize(object),
                ttl
        );
        writeCachedLog(log, new CacheDetail(cacheKey, ttl));
    }

    /**
     * 覆盖一个已有的，不需要摘要的缓存
     *
     * @param id     对象id
     * @param object 对象
     * @param ttl    缓存时间
     */
    public boolean setIfPresents(String id, T object, @Nullable Duration ttl) {
        return setIfPresents(scanNamespace(), id, scanSuffix(), object, ttl);
    }

    /**
     * 覆盖一个已有的缓存
     *
     * @param namespaces 命名空间
     * @param id         缓存id
     * @param suffix     后缀
     * @param object     缓存对象
     * @param ttl        缓存时间
     */
    public boolean setIfPresents(@Nullable String[] namespaces, String id, @Nullable String suffix, T object, @Nullable Duration ttl) {
        String cacheKey = composeCacheKey(namespaces, id, suffix);
        boolean done = this.cacheClient.setIfPresents(
                cacheKey,
                serializer.serialize(object),
                ttl
        );
        if (done) {
            writeCachedLog(log, new CacheDetail(cacheKey, ttl));
        }
        return done;
    }

    /**
     * 当不存在时设置
     *
     * @param id     对象id
     * @param object 对象
     * @param ttl    缓存时间
     */
    public boolean setIfNotPresents(String id, T object, @Nullable Duration ttl) {
        return setIfNotPresents(scanNamespace(), id, scanSuffix(), object, ttl);
    }

    /**
     * 当不存在时设置
     *
     * @param namespaces 命名空间
     * @param id         缓存id
     * @param suffix     后缀
     * @param object     缓存对象
     * @param ttl        缓存时间
     */
    public boolean setIfNotPresents(@Nullable String[] namespaces, String id, @Nullable String suffix, T object, @Nullable Duration ttl) {
        String cacheKey = composeCacheKey(namespaces, id, suffix);
        boolean done = this.cacheClient.setIfNotPresents(
                cacheKey,
                serializer.serialize(object),
                ttl
        );
        if (done) {
            writeCachedLog(log, new CacheDetail(cacheKey, ttl));
        }
        return done;
    }

    /**
     * 删除缓存
     *
     * @param id id
     */
    public void delete(String id) {
        delete(scanNamespace(), id, scanSuffix());
    }

    /**
     * 删除缓存
     *
     * @param namespaces 命名空间
     * @param id         id
     * @param suffix     后缀
     */
    public void delete(@Nullable String[] namespaces, String id, @Nullable String suffix) {
        String cacheKey = composeCacheKey(namespaces, id, suffix);
        this.cacheClient.delete(composeCacheKey(namespaces, id, suffix));
        writeCacheDeletedLog(log, new CacheDetail(cacheKey, null));
    }


    /**
     * 更新缓存时间
     *
     * @param id  id
     * @param ttl 新的缓存时间
     */
    public void updateTtl(String id, Duration ttl) {
        updateTtl(scanNamespace(), id, scanSuffix(), ttl);
    }

    /**
     * 更新缓存时间
     *
     * @param namespaces 命名空间
     * @param id         id
     * @param suffix     后缀
     * @param ttl        新的缓存时间
     */
    public void updateTtl(@Nullable String[] namespaces, String id, @Nullable String suffix, Duration ttl) {
        this.cacheClient.updateTtl(composeCacheKey(namespaces, id, suffix), ttl);
    }


    /**
     * 获得指定key的ttl
     *
     * @param id id
     */
    @Nullable
    public Duration ttl(String id) {
        return ttl(scanNamespace(), id, scanSuffix());
    }

    /**
     * 获得指定key的ttl
     *
     * @param namespaces 命名空间
     * @param id         id
     * @param suffix     后缀
     * @return ttl
     */
    @Nullable
    public Duration ttl(@Nullable String[] namespaces, String id, @Nullable String suffix) {
        return this.cacheClient.ttl(composeCacheKey(namespaces, id, suffix));
    }

    /**
     * 扫描目标类型获得命名空间
     *
     * @return 命名空间
     */
    @Nullable
    protected String[] scanNamespace() {
        Cached annotation = AnnotationUtils.findAnnotation(targetType, Cached.class);
        Assert.notNull(annotation, targetType + " must annotated with Cached");
        return annotation.namespaces();
    }

    /**
     * 扫描目标类型获得后缀
     *
     * @return 后缀
     */
    protected String scanSuffix() {
        Cached annotation = AnnotationUtils.findAnnotation(targetType, Cached.class);
        Assert.notNull(annotation, targetType + " must annotated with Cached");
        return annotation.suffix();
    }

    /**
     * 组合缓存键
     *
     * @param namespace 命名空间
     * @param id        缓存id
     * @param suffix    后缀
     * @return 缓存键
     */
    protected String composeCacheKey(@Nullable String[] namespace, String id, @Nullable String suffix) {
        List<String> keyElements = new LinkedList<>();
        if (namespace != null) {
            keyElements.addAll(Arrays.asList(namespace));
        }
        String key = String.format("{%s}%s", id, StringUtils.hasText(suffix) ? "." + suffix : "");
        keyElements.add(key);
        return String.join(":", keyElements);
    }

    /**
     * 推断序列化
     *
     * @param objectMapper 映射器
     * @return 序列化
     */
    @SuppressWarnings("unchecked")
    protected Serializer<T> deduceSerializer(@Nullable ObjectMapper objectMapper) {
        if (this.targetType.equals(String.class)) {
            return (Serializer<T>) new StringSerializer();
        }
        if (this.targetType.equals(Byte.class)) {
            return (Serializer<T>) new ByteSerializer();
        }
        if (this.targetType.equals(Short.class)) {
            return (Serializer<T>) new ShortSerializer();
        }
        if (this.targetType.equals(Integer.class)) {
            return (Serializer<T>) new IntegerSerializer();
        }
        if (this.targetType.equals(Long.class)) {
            return (Serializer<T>) new LongSerializer();
        }
        if (this.targetType.equals(Float.class)) {
            return (Serializer<T>) new FloatSerializer();
        }
        if (this.targetType.equals(Double.class)) {
            return (Serializer<T>) new DoubleSerializer();
        }
        if (objectMapper != null) {
            return new JsonSerializer<>(objectMapper, targetType);
        }
        //兜底检查是不是可以序列化
        if (Serializable.class.isAssignableFrom(this.targetType)) {
            return new JdkSerializer<>();
        }
        throw new UnsupportedOperationException("can not deduce serializer, subclass should override this method");
    }

    /**
     * 获取子类实际使用的模板参数
     *
     * @return 模板参数
     */
    @SuppressWarnings("unchecked")
    protected final Class<T> getSubclassParameterizedType() {
        Type superclass = this.getClass().getGenericSuperclass();
        return (Class<T>) TypeFactory.rawClass(((ParameterizedType) superclass).getActualTypeArguments()[0]);
    }

    /**
     * 记录一下缓存完成
     *
     * @param logger 记录器
     * @param detail 详情
     */
    protected void writeCachedLog(Logger logger, CacheDetail detail) {
        if (logger == this.enabledLogger) {
            this.enabledLogger.getName();
            this.loggingMethod.log(
                    this.enabledLogger,
                    GenericOperationLogContent.builder()
                            .what(targetType)
                            .operation(new Cache())
                            .state(new Done())
                            .detail(detail).build(),
                    null
            );
        }
    }

    /**
     * 记录一下缓存删除
     *
     * @param logger 记录器
     * @param detail 详情
     */
    protected void writeCacheDeletedLog(Logger logger, CacheDetail detail) {
        if (logger == this.enabledLogger) {
            this.loggingMethod.log(
                    this.enabledLogger,
                    GenericOperationLogContent.builder()
                            .what(targetType)
                            .operation(new Delete())
                            .state(new Done())
                            .detail(detail).build(),
                    null
            );
        }
    }

    /**
     * 设置启用的记录器
     *
     * @param logger 记录器
     */
    protected void enableLogger(Logger logger) {
        this.enabledLogger = logger;
    }
}
