package io.gardenerframework.fragrans.data.practice.operation.checker;

import io.gardenerframework.fragrans.log.common.schema.reason.NotFound;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/6/17 1:49
 */
@SuperBuilder
public abstract class BaseExistenceChecker<I, R> extends BaseChecker<I, R> {
    @Getter(AccessLevel.PROTECTED)
    private final Word how = new NotFound();

    @Override
    protected <T extends R> boolean checkCollection(Collection<T> records) {
        return !CollectionUtils.isEmpty(records);
    }

    @Override
    protected <T extends R> boolean checkEachRecord(T record) {
        return record != null;
    }
}
