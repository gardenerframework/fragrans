package com.jdcloud.gardener.fragrans.data.schema.trash;

import com.jdcloud.gardener.fragrans.data.schema.annotation.ImmutableField;
import com.jdcloud.gardener.fragrans.data.schema.common.BasicRecord;
import com.jdcloud.gardener.fragrans.data.schema.trash.trait.Item;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 垃圾箱的基本内容
 *
 * @author zhanghan30
 * @date 2021/10/22 12:29 下午
 */
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BasicTrash<I> extends BasicRecord implements Item<I> {
    /**
     * 被删除的，仍在垃圾箱中的物体
     */
    @ImmutableField
    private I item;

    protected BasicTrash(Date createdTime, Date lastUpdateTime, I item) {
        super(createdTime, lastUpdateTime);
        this.item = item;
    }
}
