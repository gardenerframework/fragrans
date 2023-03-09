package io.gardenerframework.fragrans.data.practice.operation.checker;

import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import io.gardenerframework.fragrans.log.common.schema.reason.Disabled;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/6/17 4:07 下午
 */
@SuperBuilder
public abstract class BaseEnabledStatusChecker<I, R extends GenericTraits.StatusTraits.EnableFlag> extends BaseChecker<I, R> {
    @Getter(AccessLevel.PROTECTED)
    private final Word how = new Disabled();

    @Override
    protected <T extends R> boolean checkCollection(Collection<T> records) {
        return true;
    }

    @Override
    protected <T extends R> boolean checkEachRecord(T record) {
        return record.isEnabled();
    }
}
