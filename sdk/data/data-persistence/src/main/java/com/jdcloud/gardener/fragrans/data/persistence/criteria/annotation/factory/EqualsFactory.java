package com.jdcloud.gardener.fragrans.data.persistence.criteria.annotation.factory;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BasicCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.EqualsCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.BasicValue;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/11/29 16:43
 */
@Component
public class EqualsFactory implements CriteriaFactory {

    @Override
    public BasicCriteria createCriteria(Class<?> entityType, Object criteria, String criteriaParameterName, Column column, BasicValue value) {
        return new EqualsCriteria(column, value);
    }
}
