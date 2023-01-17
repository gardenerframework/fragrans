package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.BasicValue;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.RawValue;
import lombok.Getter;

/**
 * @author zhanghan30
 * @date 2022/6/16 5:56 下午
 */
public abstract class BasicBinaryOperatorCriteria extends RawCriteria {
    @Getter
    private final Column column;
    @Getter
    private final BasicValue value;

    /**
     * 给一个不等式的条件
     *
     * @param column 列名
     * @param value  值
     */
    public BasicBinaryOperatorCriteria(String column, String value) {
        this(column, true, value);
    }

    /**
     * 给一个双目操作条件
     *
     * @param column         列名
     * @param addGraveAccent 列是否加重音
     * @param value          值
     */
    public BasicBinaryOperatorCriteria(String column, boolean addGraveAccent, String value) {
        this(new Column(column, addGraveAccent), value);
    }

    /**
     * 给一个双目操作条件
     *
     * @param column         列名
     * @param addGraveAccent 列是否加重音
     * @param value          值
     */
    public BasicBinaryOperatorCriteria(String column, boolean addGraveAccent, BasicValue value) {
        this(new Column(column, addGraveAccent), value);
    }

    /**
     * 给一个双目操作条件
     *
     * @param column 列名
     * @param value  值
     */
    public BasicBinaryOperatorCriteria(String column, BasicValue value) {
        this(new Column(column), value);
    }

    /**
     * 给一个双目操作条件
     *
     * @param column 列
     * @param value  值
     */
    public BasicBinaryOperatorCriteria(Column column, BasicValue value) {
        this.column = column;
        this.value = value;
    }

    /**
     * 给一个双目操作条件
     *
     * @param column 列
     * @param value  值
     */
    public BasicBinaryOperatorCriteria(Column column, String value) {
        this(column, new RawValue<>(value));
    }

    /**
     * 给一个双目操作条件
     *
     * @param column1 列1
     * @param column2 列2
     */
    public BasicBinaryOperatorCriteria(Column column1, Column column2) {
        this(column1, column2.build());
    }

    /**
     * 获取操作符号
     *
     * @return 操作符
     */
    protected abstract String getOperator();

    @Override
    public String build() {
        this.setCriteria(String.format("%s %s %s", column.build(), getOperator(), value.build()));
        return super.build();
    }
}
