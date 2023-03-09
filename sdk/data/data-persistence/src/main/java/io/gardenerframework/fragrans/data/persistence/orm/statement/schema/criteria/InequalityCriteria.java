package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria;

import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.Column;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.BasicValue;
import lombok.Getter;

/**
 * @author zhanghan30
 * @date 2022/6/16 5:38 下午
 */
public class InequalityCriteria extends BasicBinaryOperatorCriteria {
    private final Operator operator;

    public InequalityCriteria(String column, boolean addDelimitIdentifier, Operator operator, String value) {
        super(column, addDelimitIdentifier, value);
        this.operator = operator;
    }

    public InequalityCriteria(String column, Operator operator, String value) {
        this(column, true, operator, value);
    }

    public InequalityCriteria(String column, boolean addDelimitIdentifier, Operator operator, BasicValue value) {
        this(column, addDelimitIdentifier, operator, value.build());
    }

    public InequalityCriteria(String column, Operator operator, BasicValue value) {
        this(column, true, operator, value);
    }

    public InequalityCriteria(Column column, Operator operator, BasicValue value) {
        super(column, value);
        this.operator = operator;
    }

    public InequalityCriteria(Column column, Operator operator, String value) {
        super(column, value);
        this.operator = operator;
    }

    public InequalityCriteria(Column column1, Operator operator, Column column2) {
        super(column1, column2);
        this.operator = operator;
    }

    @Override
    protected String getOperator() {
        return operator.getValue();
    }

    public enum Operator {
        /**
         * 大于
         */
        GT("&gt;"),
        /**
         * 大于等于
         */
        GTE("&gt;="),
        /**
         * 小于
         */
        LT("&lt;"),
        /**
         * 小于等于
         */
        LTE("&lt;="),
        /**
         * 不等于
         */
        NE("&lt;&gt;");
        /**
         * 操作符
         */
        @Getter
        private final String value;

        Operator(String value) {
            this.value = value;
        }
    }
}
