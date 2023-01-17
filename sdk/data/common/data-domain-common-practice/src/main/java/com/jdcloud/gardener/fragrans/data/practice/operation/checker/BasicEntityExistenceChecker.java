package com.jdcloud.gardener.fragrans.data.practice.operation.checker;

import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/6/17 1:49
 */
@SuperBuilder
public class BasicEntityExistenceChecker<I, R extends BasicEntity<I>> extends BasicExistenceChecker<I, R> implements BasicEntityRecordIdExtractor<I, R> {
}
