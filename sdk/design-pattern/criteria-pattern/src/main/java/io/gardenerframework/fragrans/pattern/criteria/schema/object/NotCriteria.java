package io.gardenerframework.fragrans.pattern.criteria.schema.object;

import io.gardenerframework.fragrans.pattern.criteria.schema.root.BaseBooleanCriteria;
import io.gardenerframework.fragrans.pattern.criteria.schema.root.BaseNotCriteria;
import lombok.experimental.SuperBuilder;

/**
 * 非逻辑
 *
 * @author zhanghan30
 * @date 2023/1/17 17:39
 */
@SuperBuilder
public class NotCriteria<O> extends BaseNotCriteria<JavaObjectCriteria<? super O>> implements JavaObjectCriteria<O> {
    @Override
    public boolean meetCriteria(O object) {
        return !getCriteria().meetCriteria(object);
    }
}
