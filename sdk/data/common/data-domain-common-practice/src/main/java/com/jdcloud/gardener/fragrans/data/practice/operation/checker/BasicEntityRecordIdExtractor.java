package com.jdcloud.gardener.fragrans.data.practice.operation.checker;

import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import org.springframework.util.Assert;

/**
 * @author zhanghan30
 * @date 2022/6/17 3:34 下午
 */
public interface BasicEntityRecordIdExtractor<I, R extends BasicEntity<I>> extends RecordIdExtractor<I, R> {
    /**
     * 读取基础实体的id
     *
     * @param record 记录
     * @return id
     */
    @Override
    default I extractId(R record) {
        Assert.notNull(record, "record must not be null");
        return record.getId();
    }
}
