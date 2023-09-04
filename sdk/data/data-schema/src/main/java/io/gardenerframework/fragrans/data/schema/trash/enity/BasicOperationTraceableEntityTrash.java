package io.gardenerframework.fragrans.data.schema.trash.enity;

import io.gardenerframework.fragrans.data.schema.annotation.ImmutableField;
import io.gardenerframework.fragrans.data.schema.annotation.OperationTracingField;
import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 增加删除人
 *
 * @author zhanghan30
 * @date 2022/2/11 4:27 下午
 */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class BasicOperationTraceableEntityTrash<T, I> extends BasicEntityTrash<T, I> implements
        BasicEntityTrashSkeleton<T, I>,
        SecurityTraits.AuditingTraits.IdentifierTraits.Deleter {
    /**
     * 删除人
     */
    @ImmutableField
    @OperationTracingField
    private String deleter;

    protected BasicOperationTraceableEntityTrash(Date createdTime, Date lastUpdateTime, T id, I item, String deleter) {
        super(createdTime, lastUpdateTime, id, item);
        this.deleter = deleter;
    }
}
