package io.gardenerframework.fragrans.pattern.criteria.schema.root;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author zhanghan30
 * @date 2023/1/17 17:15
 */
@SuperBuilder
@Setter
@Getter
public abstract class BaseMatchAllCriteria<C extends Criteria> {
    @Singular("criteria")
    @NonNull
    private List<C> criteriaList;
}
