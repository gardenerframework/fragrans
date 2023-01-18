package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement;

import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.DatabaseCriteria;

/**
 * @author zhanghan30
 * @date 2022/6/14 6:55 下午
 */
public class DeleteStatement extends BasicStatement<DeleteStatement> {
    private DatabaseCriteria criteria;

    public DeleteStatement where(DatabaseCriteria criteria) {
        this.criteria = criteria;
        return this;
    }

    @Override
    protected String buildInternally() {
        return appendQueryCriteria(String.format("DELETE FROM %s", this.getTable().build()), criteria);
    }
}
