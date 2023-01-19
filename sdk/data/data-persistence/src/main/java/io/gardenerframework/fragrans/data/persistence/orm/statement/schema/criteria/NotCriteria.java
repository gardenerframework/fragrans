package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria;

import io.gardenerframework.fragrans.pattern.criteria.schema.root.BaseNotCriteria;

/**
 * 非条件
 */
public class NotCriteria extends BaseNotCriteria<DatabaseCriteria> implements DatabaseCriteria {
    public NotCriteria() {
        super(new DummyCriteria());
    }

    public NotCriteria(DatabaseCriteria criteria) {
        super(criteria);
    }

    public NotCriteria criteria(DatabaseCriteria criteria) {
        this.setCriteria(criteria);
        return this;
    }

    @Override
    public String build() {
        if (getCriteria() instanceof DummyCriteria) {
            return "";
        }
        return String.format("NOT (%s)", getCriteria().build());
    }

    static class DummyCriteria implements DatabaseCriteria {

        @Override
        public String build() {
            return "";
        }
    }
}

