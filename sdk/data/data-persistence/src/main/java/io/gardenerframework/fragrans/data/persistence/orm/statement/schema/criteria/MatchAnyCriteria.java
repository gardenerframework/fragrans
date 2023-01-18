package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria;

import io.gardenerframework.fragrans.pattern.criteria.schema.root.BaseMatchAnyCriteria;

import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * 条件有一个需要满足
 *
 * @author zhanghan30
 * @date 2022/9/24 02:24
 */
public class MatchAnyCriteria extends BaseMatchAnyCriteria<DatabaseCriteria> implements DatabaseCriteria {
    public MatchAnyCriteria() {
        super(new LinkedList<>());
    }

    public MatchAnyCriteria(DatabaseCriteria criteria) {
        this();
        this.getCriteriaList().add(criteria);
    }

    /**
     * 加条件
     *
     * @param criteria 条件
     * @return 当前条件
     */
    public MatchAnyCriteria or(DatabaseCriteria criteria) {
        this.getCriteriaList().add(criteria);
        return this;
    }


    @Override
    public String build() {
        return String.format("(%s)", getCriteriaList().stream().map(criteria -> String.format("(%s)", criteria.build()))
                .collect(Collectors.joining(String.format(" %s ", "OR"))));
    }
}
