package com.jdcloud.gardener.fragrans.data.persistence.criteria.annotation.factory;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BasicCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.BasicValue;

/**
 * @author ZhangHan
 * @date 2022/11/29 16:40
 */
public interface CriteriaFactory {
    /**
     * 创建搜索条件
     *
     * @param entityType            实体类型
     * @param criteria              条件参数
     * @param criteriaParameterName 条件参数名
     * @param column                引擎创建出来的lie
     * @param value                 引擎默认给定的值
     * @return 搜索条件
     */
    BasicCriteria createCriteria(
            Class<?> entityType,
            Object criteria,
            String criteriaParameterName,
            Column column, BasicValue value
    );
}
