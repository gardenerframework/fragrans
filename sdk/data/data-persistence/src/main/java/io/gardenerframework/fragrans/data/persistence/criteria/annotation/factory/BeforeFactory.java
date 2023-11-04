package io.gardenerframework.fragrans.data.persistence.criteria.annotation.factory;

import io.gardenerframework.fragrans.data.persistence.configuration.DataPersistenceComponent;
import io.gardenerframework.fragrans.data.persistence.criteria.annotation.TypeConstraints;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.Column;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.DatabaseCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.InequalityCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.BasicValue;

import java.util.Date;

/**
 * @author chris
 * <p>
 * date: 2023/11/4
 */
@TypeConstraints(Date.class)
@DataPersistenceComponent
public class BeforeFactory implements CriteriaFactory {
    @Override
    public DatabaseCriteria createCriteria(Class<?> entityType, Object criteria, String criteriaParameterName, Column column, BasicValue value) {
        return new InequalityCriteria(column, InequalityCriteria.Operator.LTE, value);
    }
}
