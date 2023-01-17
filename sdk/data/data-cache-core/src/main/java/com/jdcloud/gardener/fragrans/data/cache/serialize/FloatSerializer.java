package com.jdcloud.gardener.fragrans.data.cache.serialize;

/**
 * @author zhanghan30
 * @date 2022/2/12 12:25 下午
 */
public class FloatSerializer extends NumberSerializer<Float> {
    @Override
    protected Float toNumber(String content) {
        return Float.valueOf(content);
    }
}
