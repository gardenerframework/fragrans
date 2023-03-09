package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhanghan30
 * @date 2022/6/15 5:18 下午
 */
@AllArgsConstructor
public class RawValue<V> extends BasicValue {
    @Getter(AccessLevel.PROTECTED)
    private final V value;

    @Override
    public String build() {
        return String.valueOf(value);
    }
}
