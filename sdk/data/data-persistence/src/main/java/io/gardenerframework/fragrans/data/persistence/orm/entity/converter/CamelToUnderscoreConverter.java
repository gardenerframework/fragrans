package io.gardenerframework.fragrans.data.persistence.orm.entity.converter;

import com.google.common.base.CaseFormat;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/6/14 5:18 下午
 */
@Component
public class CamelToUnderscoreConverter implements ColumnNameConverter {
    @Override
    public String fieldToColumn(String field) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field);
    }

    @Override
    public String columnToField(String column) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, column);
    }
}
