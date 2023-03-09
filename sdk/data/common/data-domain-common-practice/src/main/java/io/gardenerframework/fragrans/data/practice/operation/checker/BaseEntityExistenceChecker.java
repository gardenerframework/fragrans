package io.gardenerframework.fragrans.data.practice.operation.checker;

import io.gardenerframework.fragrans.data.schema.entity.BasicEntity;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/6/17 1:49
 */
@SuperBuilder
public abstract class BaseEntityExistenceChecker<I, R extends BasicEntity<I>> extends BaseExistenceChecker<I, R> implements BasicEntityRecordIdExtractor<I, R> {
}
