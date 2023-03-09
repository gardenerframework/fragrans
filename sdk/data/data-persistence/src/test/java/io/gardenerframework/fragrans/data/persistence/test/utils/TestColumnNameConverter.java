package io.gardenerframework.fragrans.data.persistence.test.utils;

import io.gardenerframework.fragrans.data.persistence.orm.entity.converter.ColumnNameConverter;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/6/14 5:43 下午
 */
@Component
public class TestColumnNameConverter implements ColumnNameConverter {

    @Override
    public String fieldToColumn(String field) {
        return "all-same";
    }

    @Override
    public String columnToField(String column) {
        return null;
    }
}
