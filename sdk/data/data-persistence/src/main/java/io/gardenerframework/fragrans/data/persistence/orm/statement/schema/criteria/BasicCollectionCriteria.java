package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/9/24 02:26
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BasicCollectionCriteria implements DatabaseCriteria {
    private final Collection<DatabaseCriteria> criteriaList = new LinkedList<>();

    protected BasicCollectionCriteria(DatabaseCriteria criteria) {
        criteriaList.add(criteria);
    }

    protected void add(DatabaseCriteria criteria) {
        criteriaList.add(criteria);
    }

    protected abstract String getOperator();

    public boolean isEmpty() {
        return criteriaList.isEmpty();
    }

    @Override
    public String build() {
        return String.format("(%s)", criteriaList.stream().map(criteria -> String.format("(%s)", criteria.build()))
                .collect(Collectors.joining(String.format(" %s ", getOperator()))));
    }
}
