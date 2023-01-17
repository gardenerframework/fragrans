package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BasicCriteria;

/**
 * @author zhanghan30
 * @date 2022/6/14 6:55 下午
 */
public class DeleteStatement extends BasicStatement<DeleteStatement> {
    private BasicCriteria criteria;

    public DeleteStatement where(BasicCriteria criteria) {
        this.criteria = criteria;
        return this;
    }

    @Override
    protected String buildInternally() {
        return appendQueryCriteria(String.format("DELETE FROM %s", this.getTable().build()), criteria);
    }
}
