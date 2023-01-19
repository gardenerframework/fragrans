package io.gardenerframework.fragrans.data.persistence.test;

import io.gardenerframework.fragrans.data.persistence.test.utils.fieldTest.FieldTestDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ZhangHan
 * @date 2021/8/22 0:16
 */
@SpringBootApplication
@MapperScan(basePackageClasses = FieldTestDao.class)
public class DataPersistenceTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataPersistenceTestApplication.class, args);
    }
}
