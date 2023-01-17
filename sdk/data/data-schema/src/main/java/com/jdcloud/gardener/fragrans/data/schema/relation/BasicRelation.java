package com.jdcloud.gardener.fragrans.data.schema.relation;

import com.jdcloud.gardener.fragrans.data.schema.common.BasicRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 基本的关系定义
 * <p>
 * 标记性类，用来区分实体
 *
 * @author zhanghan
 * @date 2021/3/29 21:57
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class BasicRelation extends BasicRecord {
    protected BasicRelation(Date createdTime, Date lastUpdateTime) {
        super(createdTime, lastUpdateTime);
    }
}
