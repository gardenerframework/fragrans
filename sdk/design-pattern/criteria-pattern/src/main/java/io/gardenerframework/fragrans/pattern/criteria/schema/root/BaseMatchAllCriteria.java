package io.gardenerframework.fragrans.pattern.criteria.schema.root;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author zhanghan30
 * @date 2023/1/17 17:15
 */
@SuperBuilder
@Setter
@Getter
@AllArgsConstructor
public abstract class BaseMatchAllCriteria<C extends Criteria> {
    @Singular("criteria")
    @NonNull
    private List<C> criteriaList;


    /**
     * 是否没有任何条件
     *
     * @return 是否没有条件
     */
    public boolean isEmpty() {
        return criteriaList.isEmpty();
    }
}
