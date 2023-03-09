package io.gardenerframework.fragrans.pattern.criteria.schema.object;

import io.gardenerframework.fragrans.pattern.criteria.schema.root.BaseMatchAllCriteria;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author zhanghan30
 * @date 2023/1/17 17:18
 */
@SuperBuilder
public class MatchAnyCriteria<O> extends BaseMatchAllCriteria<JavaObjectCriteria<? super O>> implements JavaObjectCriteria<O> {
    public MatchAnyCriteria(@NonNull List<JavaObjectCriteria<? super O>> criteriaList) {
        super(criteriaList);
    }

    @Override
    public boolean meetCriteria(O object) {
        for (JavaObjectCriteria<? super O> criteria : getCriteriaList()) {
            if (criteria.meetCriteria(object)) {
                return true;
            }
        }
        return false;
    }
}
