package io.gardenerframework.fragrans.data.persistence.criteria.annotation.factory;

import io.gardenerframework.fragrans.data.persistence.configuration.DataPersistenceComponent;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.Column;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.DatabaseCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.EqualsCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.BasicValue;

/**
 * @author ZhangHan
 * @date 2022/11/29 16:43
 */
@DataPersistenceComponent
public class EqualsFactory implements CriteriaFactory {

    @Override
    public DatabaseCriteria createCriteria(Class<?> entityType, Object criteria, String criteriaParameterName, Column column, BasicValue value) {
        return new EqualsCriteria(column, value);
    }
}
