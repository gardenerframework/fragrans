package io.gardenerframework.fragrans.data.practice.operation.checker;

/**
 * @author zhanghan30
 * @date 2022/6/17 3:34 下午
 */
public interface RecordIdExtractor<I, R> {
    /**
     * 抽取记录id
     *
     * @param record 记录
     * @return id
     */
    I extractId(R record);
}
