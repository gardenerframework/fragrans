package com.jdcloud.gardener.fragrans.data.cache.serialize;

/**
 * @author zhanghan30
 * @date 2022/2/12 12:25 下午
 */
public class ShortSerializer extends NumberSerializer<Short> {
    @Override
    protected Short toNumber(String content) {
        return Short.valueOf(content);
    }
}
