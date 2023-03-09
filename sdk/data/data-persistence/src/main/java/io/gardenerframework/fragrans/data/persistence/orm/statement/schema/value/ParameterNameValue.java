package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value;

import lombok.AllArgsConstructor;

/**
 * @author zhanghan30
 * @date 2022/6/15 7:10 下午
 */
@AllArgsConstructor
public class ParameterNameValue extends BasicValue {
    private final String name;

    @Override
    public String build() {
        return String.format("#{%s}", name);
    }
}
