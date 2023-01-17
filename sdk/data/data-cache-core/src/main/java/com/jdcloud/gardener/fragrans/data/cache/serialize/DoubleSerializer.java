package com.jdcloud.gardener.fragrans.data.cache.serialize;

/**
 * @author zhanghan30
 * @date 2022/2/12 12:25 下午
 */
public class DoubleSerializer extends NumberSerializer<Double> {
    @Override
    protected Double toNumber(String content) {
        return Double.valueOf(content);
    }
}
