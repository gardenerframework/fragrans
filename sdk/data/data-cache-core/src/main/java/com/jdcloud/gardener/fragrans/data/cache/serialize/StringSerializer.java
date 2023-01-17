package com.jdcloud.gardener.fragrans.data.cache.serialize;

import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;

/**
 * 负责序列化和反序列化字符串
 *
 * @author zhanghan30
 * @date 2022/2/12 11:48 上午
 */
public class StringSerializer implements Serializer<String> {
    @Override
    public byte[] serialize(String object) {
        return object.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Nullable
    public String deserialize(@Nullable byte[] content) {
        return content == null ? null : new String(content, StandardCharsets.UTF_8);
    }
}
