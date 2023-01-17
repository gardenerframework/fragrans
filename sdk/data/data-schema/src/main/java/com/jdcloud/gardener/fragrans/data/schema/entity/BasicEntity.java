package com.jdcloud.gardener.fragrans.data.schema.entity;

import com.jdcloud.gardener.fragrans.data.schema.annotation.ImmutableField;
import com.jdcloud.gardener.fragrans.data.schema.common.BasicRecord;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 实体可以被认为是所有数据的基础，
 * 它包含了所有数据都应当具备的属性
 *
 * @author zhanghan
 * @date 2021/3/29 21:57
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class BasicEntity<T> extends BasicRecord implements
        GenericTraits.IdentifierTraits.Id<T> {
    /**
     * 实体的识别符号
     */
    @ImmutableField
    private T id;

    protected BasicEntity(Date createdTime, Date lastUpdateTime, T id) {
        super(createdTime, lastUpdateTime);
        this.id = id;
    }
}
