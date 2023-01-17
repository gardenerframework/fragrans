package com.jdcloud.gardener.fragrans.data.schema.trash.enity;

import com.jdcloud.gardener.fragrans.data.schema.annotation.ImmutableField;
import com.jdcloud.gardener.fragrans.data.schema.trash.BasicTrash;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 实体垃圾箱的内容
 *
 * @author zhanghan30
 * @date 2021/10/22 12:29 下午
 */
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BasicEntityTrash<T, I> extends BasicTrash<I> implements
        GenericTraits.IdentifierTraits.Id<T> {
    /**
     * 被删除的，仍在垃圾箱中的物体
     */
    @ImmutableField
    private T id;

    protected BasicEntityTrash(Date createdTime, Date lastUpdateTime, T id, I item) {
        super(createdTime, lastUpdateTime, item);
        this.id = id;
    }
}
