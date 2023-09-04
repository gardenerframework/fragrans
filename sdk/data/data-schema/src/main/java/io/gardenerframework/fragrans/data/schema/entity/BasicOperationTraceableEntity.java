package io.gardenerframework.fragrans.data.schema.entity;

import io.gardenerframework.fragrans.data.schema.annotation.ImmutableField;
import io.gardenerframework.fragrans.data.schema.annotation.OperationTracingField;
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
public abstract class BasicOperationTraceableEntity<T> extends BasicEntity<T> implements
        BasicOperationTraceableEntitySkeleton<T> {
    /**
     * 创建人
     */
    @ImmutableField
    private String creator;
    /**
     * 上一次更新人
     */
    @OperationTracingField
    private String updater;

    protected BasicOperationTraceableEntity(Date createdTime, Date lastUpdateTime, T id, String creator, String updater) {
        super(createdTime, lastUpdateTime, id);
        this.creator = creator;
        this.updater = updater;
    }
}