package io.gardenerframework.fragrans.data.cache.serialize;

/**
 * @author zhanghan30
 * @date 2022/2/12 12:25 下午
 */
public class IntegerSerializer extends NumberSerializer<Integer> {
    @Override
    protected Integer toNumber(String content) {
        return Integer.valueOf(content);
    }
}
