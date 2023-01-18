package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria;

import io.gardenerframework.fragrans.pattern.criteria.schema.root.BaseBooleanCriteria;

/**
 * @author zhanghan30
 * @date 2022/6/14 7:02 下午
 */

public class BooleanCriteria extends BaseBooleanCriteria<DatabaseCriteria> implements DatabaseCriteria {

    public BooleanCriteria() {
        super(new DummyCriteria(), new DummyCriteria(), Operator.AND);
    }

    public BooleanCriteria a(DatabaseCriteria a) {
        this.setA(a);
        return this;
    }

    public BooleanCriteria b(DatabaseCriteria b) {
        this.setB(b);
        return this;
    }

    public BooleanCriteria and() {
        this.setOperator(BaseBooleanCriteria.Operator.AND);
        return this;
    }

    public BooleanCriteria or() {
        this.setOperator(BaseBooleanCriteria.Operator.OR);
        return this;
    }

    @Override
    public String build() {
        //并没有初始化
        if (getA() instanceof DummyCriteria || getB() instanceof DummyCriteria) {
            return "";
        }
        return String.format("(%s) %s (%s)", getA().build(), getOperator(), getB().build());
    }

    static class DummyCriteria implements DatabaseCriteria {

        @Override
        public String build() {
            return "";
        }
    }
}
