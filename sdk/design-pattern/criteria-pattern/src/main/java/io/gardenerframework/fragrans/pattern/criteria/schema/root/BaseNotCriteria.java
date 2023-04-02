package io.gardenerframework.fragrans.pattern.criteria.schema.root;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2023/1/17 17:38
 */
@SuperBuilder
@Setter
@Getter
@AllArgsConstructor
public abstract class BaseNotCriteria<C extends Criteria> {
    @NonNull
    private C criteria;
}
