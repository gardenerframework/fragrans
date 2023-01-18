package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2022/6/15 2:20 下午
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RawCriteria implements DatabaseCriteria {
    @Setter(AccessLevel.PROTECTED)
    private String criteria;

    @Override
    public String build() {
        return criteria;
    }
}
