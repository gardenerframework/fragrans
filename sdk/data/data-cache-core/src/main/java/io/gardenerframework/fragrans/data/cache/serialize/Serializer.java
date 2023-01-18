package io.gardenerframework.fragrans.data.cache.serialize;

import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/2/12 11:46 上午
 */
public interface Serializer<T> {
    /**
     * 序列化对象
     *
     * @param object 对象
     * @return 序列化结果
     */
    byte[] serialize(T object);

    /**
     * 反序列化对象
     *
     * @param content 内容
     * @return 反序列化结果
     */
    @Nullable
    T deserialize(@Nullable byte[] content);
}
