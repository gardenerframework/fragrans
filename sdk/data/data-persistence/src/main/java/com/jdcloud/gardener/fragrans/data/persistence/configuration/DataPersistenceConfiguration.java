package com.jdcloud.gardener.fragrans.data.persistence.configuration;

import com.jdcloud.gardener.fragrans.data.persistence.DataPersistencePackage;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScanner;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.StatementBuilder;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.StatementBuilderStaticAccessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/6/14 5:32 下午
 */
@Configuration
@ComponentScan(basePackageClasses = DataPersistencePackage.class)
public class DataPersistenceConfiguration {

    /**
     * 保持静态生成的和bean一致
     *
     * @return bean
     */
    @Bean
    public FieldScanner fieldScanner() {
        return FieldScannerStaticAccessor.scanner();
    }

    /**
     * 保持静态生成的和bean一致
     *
     * @return bean
     */
    @Bean
    public StatementBuilder statementBuilder() {
        return StatementBuilderStaticAccessor.builder();
    }
}
