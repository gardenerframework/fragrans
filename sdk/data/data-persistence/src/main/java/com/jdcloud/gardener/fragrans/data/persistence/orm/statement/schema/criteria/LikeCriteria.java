package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.BasicValue;

/**
 * 等式
 *
 * @author zhanghan30
 * @date 2022/6/16 5:19 下午
 */
public class LikeCriteria extends BasicBinaryOperatorCriteria {
    public LikeCriteria(String column, String value) {
        super(column, value);
    }

    public LikeCriteria(String column, boolean addGraveAccent, String value) {
        super(column, addGraveAccent, value);
    }

    public LikeCriteria(String column, boolean addGraveAccent, BasicValue value) {
        super(column, addGraveAccent, value);
    }

    public LikeCriteria(String column, BasicValue value) {
        super(column, value);
    }

    public LikeCriteria(Column column, BasicValue value) {
        super(column, value);
    }

    public LikeCriteria(Column column, String value) {
        super(column, value);
    }

    public LikeCriteria(Column column1, Column column2) {
        super(column1, column2);
    }

    @Override
    protected String getOperator() {
        return "LIKE";
    }
}
