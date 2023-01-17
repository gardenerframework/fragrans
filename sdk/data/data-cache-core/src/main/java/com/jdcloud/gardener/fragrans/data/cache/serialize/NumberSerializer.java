package com.jdcloud.gardener.fragrans.data.cache.serialize;

import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;

/**
 * 用于基本的整数，长整数等数据的序列化和反序列化
 *
 * @author zhanghan30
 * @date 2022/2/12 12:21 下午
 */
public abstract class NumberSerializer<T extends Number> implements Serializer<T> {
    /**
     * 从字符串转为数字
     *
     * @param content 内容
     * @return 数字
     */
    protected abstract T toNumber(String content);

    @Override
    public byte[] serialize(T object) {
        return String.valueOf(object).getBytes(StandardCharsets.UTF_8);
    }

    @Nullable
    @Override
    public T deserialize(@Nullable byte[] content) {
        return content == null ? null : toNumber(new String(content, StandardCharsets.UTF_8));
    }
}
