package io.gardenerframework.fragrans.pattern.criteria.schema.object;

import io.gardenerframework.fragrans.pattern.criteria.schema.root.BaseBooleanCriteria;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2023/1/17 17:07
 */
@SuperBuilder
public class BooleanCriteria<O> extends BaseBooleanCriteria<JavaObjectCriteria<? super O>> implements JavaObjectCriteria<O> {

    public BooleanCriteria(@NonNull JavaObjectCriteria<? super O> a, @NonNull JavaObjectCriteria<? super O> b, @NonNull Operator operator) {
        super(a, b, operator);
    }

    @Override
    public boolean meetCriteria(O object) {
        Operator operator = getOperator();
        switch (operator) {
            case AND:
                return getA().meetCriteria(object) && getB().meetCriteria(object);
            case OR:
                return getA().meetCriteria(object) || getB().meetCriteria(object);
            default:
                throw new IllegalStateException("unknown operator " + operator);
        }
    }
}
