package io.gardenerframework.fragrans.data.cache.serialize;

import org.springframework.lang.Nullable;

import java.io.*;

/**
 * @author zhanghan30
 * @date 2022/2/14 6:30 下午
 */
public class JdkSerializer<T> implements Serializer<T> {
    @Override
    public byte[] serialize(T object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(outputStream).writeObject(object);
            outputStream.flush();
            byte[] result = outputStream.toByteArray();
            outputStream.close();
            return result;
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(@Nullable byte[] content) {
        if (content == null) {
            return null;
        }
        try {
            return (T) new ObjectInputStream(new ByteArrayInputStream(content)).readObject();
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
