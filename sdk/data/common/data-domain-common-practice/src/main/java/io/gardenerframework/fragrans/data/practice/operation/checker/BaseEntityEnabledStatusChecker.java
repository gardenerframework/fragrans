package io.gardenerframework.fragrans.data.practice.operation.checker;

import io.gardenerframework.fragrans.data.schema.entity.BasicEntity;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/6/17 4:07 下午
 */
@SuperBuilder
public abstract class BaseEntityEnabledStatusChecker<I, R extends BasicEntity<I> & GenericTraits.StatusTraits.EnableFlag> extends BaseEnabledStatusChecker<I, R> implements BasicEntityRecordIdExtractor<I, R> {
}
