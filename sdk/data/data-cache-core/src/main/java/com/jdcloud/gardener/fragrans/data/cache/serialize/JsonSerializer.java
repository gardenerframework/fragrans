package com.jdcloud.gardener.fragrans.data.cache.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author zhanghan30
 * @date 2022/2/14 6:11 下午
 */
public class JsonSerializer<T> implements Serializer<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> targetType;

    public JsonSerializer(ObjectMapper objectMapper) {
        this(objectMapper, null);
    }

    public JsonSerializer(ObjectMapper objectMapper, @Nullable Class<T> targetType) {
        this.objectMapper = objectMapper;
        this.targetType = targetType == null ? getSubclassParameterizedType() : targetType;
    }

    /**
     * 获取子类实际使用的模板参数
     *
     * @return 模板参数
     */
    @SuppressWarnings("unchecked")
    private Class<T> getSubclassParameterizedType() {
        Type superclass = this.getClass().getGenericSuperclass();
        return (Class<T>) TypeFactory.rawClass(((ParameterizedType) superclass).getActualTypeArguments()[0]);
    }

    @Override
    public byte[] serialize(T object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Nullable
    @Override
    public T deserialize(@Nullable byte[] content) {
        try {
            return content == null ? null : objectMapper.readValue(content, targetType);
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
