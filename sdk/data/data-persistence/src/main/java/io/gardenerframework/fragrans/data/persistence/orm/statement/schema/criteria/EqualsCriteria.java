package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria;

import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.Column;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.BasicValue;

/**
 * 等式
 *
 * @author zhanghan30
 * @date 2022/6/16 5:19 下午
 */
public class EqualsCriteria extends BasicBinaryOperatorCriteria {
    public EqualsCriteria(String column, String value) {
        super(column, value);
    }

    public EqualsCriteria(String column, boolean addDelimitIdentifier, String value) {
        super(column, addDelimitIdentifier, value);
    }

    public EqualsCriteria(String column, boolean addDelimitIdentifier, BasicValue value) {
        super(column, addDelimitIdentifier, value);
    }

    public EqualsCriteria(String column, BasicValue value) {
        super(column, value);
    }

    public EqualsCriteria(Column column, BasicValue value) {
        super(column, value);
    }

    public EqualsCriteria(Column column, String value) {
        super(column, value);
    }

    public EqualsCriteria(Column column1, Column column2) {
        super(column1, column2);
    }

    @Override
    protected String getOperator() {
        return "=";
    }
}
