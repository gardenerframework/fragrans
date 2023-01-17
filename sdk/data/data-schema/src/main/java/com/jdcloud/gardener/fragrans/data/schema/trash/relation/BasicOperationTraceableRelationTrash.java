package com.jdcloud.gardener.fragrans.data.schema.trash.relation;

import com.jdcloud.gardener.fragrans.data.schema.annotation.ImmutableField;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
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
public abstract class BasicOperationTraceableRelationTrash<I> extends BasicRelationTrash<I> implements
        SecurityTraits.AuditingTraits.IdentifierTraits.Deleter {
    /**
     * 删除人
     */
    @ImmutableField
    private String deleter;

    protected BasicOperationTraceableRelationTrash(Date createdTime, Date lastUpdateTime, I item, String deleter) {
        super(createdTime, lastUpdateTime, item);
        this.deleter = deleter;
    }
}
