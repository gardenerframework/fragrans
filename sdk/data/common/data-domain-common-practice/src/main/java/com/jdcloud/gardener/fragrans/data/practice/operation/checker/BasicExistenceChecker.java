package com.jdcloud.gardener.fragrans.data.practice.operation.checker;

import com.jdcloud.gardener.fragrans.log.common.schema.reason.NotFound;
import com.jdcloud.gardener.fragrans.log.schema.word.Word;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

/**
 * @author ZhangHan
 * @date 2022/6/17 1:49
 */
@SuperBuilder
public abstract class BasicExistenceChecker<I, R> extends BasicChecker<I, R> {
    @Override
    protected Word getLogHow() {
        return new NotFound();
    }


    /**
     * 判断记录是否存在
     *
     * @param record 记录
     * @return 不符合的记录id
     */
    @Override
    protected boolean doCheck(@Nullable R record) {
        return record != null;
    }

    @Override
    protected void init() {
        this.setFailOnEmptyRecordCollection(true);
        this.setFailOnNonEmptyRecordCollection(false);
    }
}
