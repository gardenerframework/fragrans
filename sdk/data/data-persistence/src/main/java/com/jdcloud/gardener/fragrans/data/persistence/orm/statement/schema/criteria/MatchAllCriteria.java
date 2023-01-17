package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria;

import lombok.NoArgsConstructor;

/**
 * 条件必须都满足
 *
 * @author zhanghan30
 * @date 2022/9/24 02:24
 */
@NoArgsConstructor
public class MatchAllCriteria extends BasicCollectionCriteria {
    public MatchAllCriteria(BasicCriteria criteria) {
        super(criteria);
    }

    /**
     * 加条件
     *
     * @param criteria 条件
     * @return 当前条件
     */
    public MatchAllCriteria and(BasicCriteria criteria) {
        add(criteria);
        return this;
    }

    @Override
    protected String getOperator() {
        return "AND";
    }
}
