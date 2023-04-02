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
public class MatchAllCriteria<O> extends BaseMatchAllCriteria<JavaObjectCriteria<? super O>> implements JavaObjectCriteria<O> {
    public MatchAllCriteria(@NonNull List<JavaObjectCriteria<? super O>> criteriaList) {
        super(criteriaList);
    }

    @Override
    public boolean meetCriteria(O object) {
        for (JavaObjectCriteria<? super O> criteria : getCriteriaList()) {
            if (!criteria.meetCriteria(object)) {
                return false;
            }
        }
        return true;
    }
}
