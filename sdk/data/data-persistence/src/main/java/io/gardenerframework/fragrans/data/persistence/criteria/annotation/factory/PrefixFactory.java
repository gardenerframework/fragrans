package io.gardenerframework.fragrans.data.persistence.criteria.annotation.factory;

import io.gardenerframework.fragrans.data.persistence.configuration.DataPersistenceComponent;
import io.gardenerframework.fragrans.data.persistence.criteria.annotation.TypeConstraints;
import io.gardenerframework.fragrans.data.persistence.orm.database.Database;
import io.gardenerframework.fragrans.data.persistence.orm.statement.exception.UnsupportedDriverException;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.Column;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.DatabaseCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.LikeCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.BasicValue;
import org.springframework.boot.jdbc.DatabaseDriver;

/**
 * @author chris
 * @date 2023/11/3
 */
@DataPersistenceComponent
@TypeConstraints(String.class)
public class PrefixFactory implements CriteriaFactory {
    @Override
    public DatabaseCriteria createCriteria(Class<?> entityType, Object criteria, String criteriaParameterName, Column column, BasicValue value) {
        DatabaseDriver driver = Database.getDriver();
        switch (driver) {
            case MYSQL:
                return new LikeCriteria(column, new BasicValue() {
                    @Override
                    public String build() {
                        return String.format("concat(%s, '%%')", value.build());
                    }
                });
            default:
                throw new UnsupportedDriverException(driver);
        }

    }
}
