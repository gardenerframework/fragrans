package io.gardenerframework.fragrans.data.cache.serialize;

/**
 * @author zhanghan30
 * @date 2022/2/12 12:25 下午
 */
public class ByteSerializer extends NumberSerializer<Byte> {
    @Override
    protected Byte toNumber(String content) {
        return Byte.valueOf(content);
    }
}
