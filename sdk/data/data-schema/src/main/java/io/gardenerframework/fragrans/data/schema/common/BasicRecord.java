package io.gardenerframework.fragrans.data.schema.common;

import io.gardenerframework.fragrans.data.schema.annotation.DatabaseControlledField;
import io.gardenerframework.fragrans.data.schema.annotation.ImmutableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 基本的记录定义，包含了记录最基本应当具备的所有字段
 *
 * @author zhanghan
 * @date 2021/3/29 21:57
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BasicRecord implements
        BasicRecordSkeleton {
    /**
     * 记录的创建时间，一般存储在落地表内，可由数据库产生默认值。
     * 创建时间一旦生成也是不可变的，请编程人员遵守这项设计
     */
    @ImmutableField
    @DatabaseControlledField
    private Date createdTime;
    /**
     * 上一次更新属性的时间，
     * 在落地表中可由数据库的特性来保证
     */
    @DatabaseControlledField
    private Date lastUpdateTime;
}
