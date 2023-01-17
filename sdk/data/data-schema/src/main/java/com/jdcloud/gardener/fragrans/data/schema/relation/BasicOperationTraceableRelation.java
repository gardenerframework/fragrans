package com.jdcloud.gardener.fragrans.data.schema.relation;

import com.jdcloud.gardener.fragrans.data.schema.annotation.ImmutableField;
import com.jdcloud.gardener.fragrans.data.schema.annotation.OperationTracingField;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 增加创建人、更新人的字段
 *
 * @author zhanghan
 * @date 2021/3/29 21:57
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class BasicOperationTraceableRelation extends BasicRelation implements
        SecurityTraits.AuditingTraits.IdentifierTraits.Creator,
        SecurityTraits.AuditingTraits.IdentifierTraits.Updater {
    /**
     * 创建人
     */
    @ImmutableField
    @OperationTracingField
    private String creator;
    /**
     * 上一次更新人
     */
    @OperationTracingField
    private String updater;

    protected BasicOperationTraceableRelation(Date createdTime, Date lastUpdateTime, String creator, String updater) {
        super(createdTime, lastUpdateTime);
        this.creator = creator;
        this.updater = updater;
    }
}