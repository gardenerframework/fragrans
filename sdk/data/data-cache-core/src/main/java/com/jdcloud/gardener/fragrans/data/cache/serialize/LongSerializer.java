package com.jdcloud.gardener.fragrans.data.cache.serialize;

/**
 * @author zhanghan30
 * @date 2022/2/12 12:25 下午
 */
public class LongSerializer extends NumberSerializer<Long> {
    @Override
    protected Long toNumber(String content) {
        return Long.valueOf(content);
    }
}
