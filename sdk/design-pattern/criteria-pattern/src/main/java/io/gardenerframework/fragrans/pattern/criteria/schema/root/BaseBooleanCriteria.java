package io.gardenerframework.fragrans.pattern.criteria.schema.root;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 常见的布尔条件语句
 *
 * @author zhanghan30
 * @date 2023/1/17 17:03
 */
@SuperBuilder
@Setter
@Getter
@AllArgsConstructor
public abstract class BaseBooleanCriteria<C extends Criteria> {
    /**
     * 条件a
     */
    @NonNull
    private C a;
    /**
     * 条件b
     */
    @NonNull
    private C b;
    /**
     * 操作符号
     */
    @NonNull
    private Operator operator;

    public enum Operator {
        /**
         * 且
         */
        AND,
        /**
         * 或
         */
        OR
    }
}
