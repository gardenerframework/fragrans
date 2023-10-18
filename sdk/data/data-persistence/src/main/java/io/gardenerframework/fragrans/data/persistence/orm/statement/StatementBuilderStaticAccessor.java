package io.gardenerframework.fragrans.data.persistence.orm.statement;

import io.gardenerframework.fragrans.data.persistence.configuration.DataPersistenceComponent;
import org.springframework.stereotype.Component;

/**
 * 一个静态的访问类
 *
 * @author ZhangHan
 * @date 2022/6/16 0:46
 */
@DataPersistenceComponent
public class StatementBuilderStaticAccessor {

    /**
     * 实际的builder
     */
    private static final StatementBuilder builder = new StatementBuilder();

    public static StatementBuilder builder() {

        return builder;
    }
}
