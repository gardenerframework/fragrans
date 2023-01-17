package io.gardenerframework.fragrans.pattern.criteria.schema.persistence;

/**
 * @author zhanghan30
 * @date 2023/1/17 17:24
 */
public interface PersistenceQueryCriteria<T> {
    /**
     * 构造成目标持久化引擎能够支持的查询语句
     *
     * @return 查询语句
     */
    T build();
}
