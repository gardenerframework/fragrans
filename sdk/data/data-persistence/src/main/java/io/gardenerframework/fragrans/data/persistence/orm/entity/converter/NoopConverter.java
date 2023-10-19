package io.gardenerframework.fragrans.data.persistence.orm.entity.converter;

import io.gardenerframework.fragrans.data.persistence.configuration.DataPersistenceComponent;

/**
 * 什么也不做的一个转换器
 *
 * @author zhanghan30
 * @date 2022/9/24 01:42
 */
@DataPersistenceComponent
public class NoopConverter implements ColumnNameConverter {
    @Override
    public String fieldToColumn(String field) {
        return field;
    }

    @Override
    public String columnToField(String column) {
        return column;
    }
}
