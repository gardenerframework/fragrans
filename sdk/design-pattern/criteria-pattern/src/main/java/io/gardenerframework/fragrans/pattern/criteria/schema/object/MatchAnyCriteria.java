package io.gardenerframework.fragrans.pattern.criteria.schema.object;

import io.gardenerframework.fragrans.pattern.criteria.schema.root.BaseMatchAllCriteria;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2023/1/17 17:18
 */
@SuperBuilder
public class MatchAnyCriteria<O> extends BaseMatchAllCriteria<JavaObjectCriteria<? super O>> implements JavaObjectCriteria<O> {
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
