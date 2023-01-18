package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhanghan30
 * @date 2022/6/15 4:41 下午
 */
@AllArgsConstructor
public class FieldNameValue extends BasicValue {
    @Getter
    private final String owner;
    @Getter
    private final String field;


    @Override
    public String build() {
        return String.format("#{%s}", owner + "." + field);
    }
}
