package com.jdcloud.gardener.fragrans.data.practice.operation.checker;

import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/6/17 4:07 下午
 */
@SuperBuilder
public class BasicEntityEnabledStatusChecker<I, R extends BasicEntity<I> & GenericTraits.StatusTraits.EnableFlag> extends BasicEnabledStatusChecker<I, R> implements BasicEntityRecordIdExtractor<I, R> {
}
