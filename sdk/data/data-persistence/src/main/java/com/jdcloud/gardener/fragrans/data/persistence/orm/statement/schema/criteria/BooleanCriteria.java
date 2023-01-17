package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria;

/**
 * @author zhanghan30
 * @date 2022/6/14 7:02 下午
 */
public class BooleanCriteria extends BasicCriteria {
    private BasicCriteria a;
    private BasicCriteria b;
    private Operator operator;

    public BooleanCriteria a(BasicCriteria a) {
        this.a = a;
        return this;
    }

    public BooleanCriteria b(BasicCriteria b) {
        this.b = b;
        return this;
    }

    public BooleanCriteria and() {
        this.operator = Operator.AND;
        return this;
    }

    public BooleanCriteria or() {
        this.operator = Operator.OR;
        return this;
    }

    @Override
    public String build() {
        return String.format("(%s) %s (%s)", a.build(), operator.toString(), b.build());
    }

    public enum Operator {
        /**
         * and
         */
        AND,
        /**
         * or
         */
        OR
    }
}
