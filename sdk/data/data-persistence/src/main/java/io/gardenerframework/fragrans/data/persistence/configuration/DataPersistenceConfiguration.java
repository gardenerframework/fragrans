package io.gardenerframework.fragrans.data.persistence.configuration;

import io.gardenerframework.fragrans.data.persistence.DataPersistencePackage;
import io.gardenerframework.fragrans.data.persistence.orm.database.Database;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/6/14 5:32 下午
 */
@Configuration
@ComponentScan(basePackageClasses = DataPersistencePackage.class, includeFilters = @ComponentScan.Filter(DataPersistenceComponent.class))
public class DataPersistenceConfiguration {
    public DataPersistenceConfiguration(DataSourceProperties dataSourceProperties) {
        Database.setDriver(DatabaseDriver.fromJdbcUrl(dataSourceProperties.getUrl()));
    }
}
