package io.gardenerframework.fragrans.api.options.persistence.configuration;

import io.gardenerframework.fragrans.api.options.persistence.DatabaseApiOptionPersistenceService;
import io.gardenerframework.fragrans.api.options.persistence.dao.ApiOptionDao;
import io.gardenerframework.fragrans.api.options.persistence.dao.ApiOptionMysqlDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ZhangHan
 * @date 2022/5/15 0:53
 */
@Configuration
@MapperScan(basePackageClasses = ApiOptionMysqlDao.class, markerInterface = ApiOptionDao.class)
@Import(DatabaseApiOptionPersistenceService.class)
public class ApiOptionDatabasePersistenceConfiguration {
}
