package io.gardenerframework.fragrans.data.schema.trash.relation;

import io.gardenerframework.fragrans.data.schema.trash.BasicTrash;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 基本关系垃圾箱
 *
 * @author zhanghan30
 * @date 2022/2/11 4:43 下午
 */
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BasicRelationTrash<I> extends BasicTrash<I> {

    protected BasicRelationTrash(Date createdTime, Date lastUpdateTime, I item) {
        super(createdTime, lastUpdateTime, item);
    }
}
