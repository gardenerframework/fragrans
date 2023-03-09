package io.gardenerframework.fragrans.pattern.criteria.schema.persistence;

import io.gardenerframework.fragrans.pattern.criteria.schema.root.Criteria;

/**
 * 已经持久化的对象
 *
 * @author zhanghan30
 * @date 2023/1/17 17:24
 */
public interface RecordCriteria<T> extends Criteria {
    /**
     * 构造成目标持久化引擎能够支持的查询语句
     *
     * @return 查询语句
     */
    T build();
}
