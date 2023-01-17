package com.jdcloud.gardener.fragrans.data.schema.attribute;

import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 实体的扩展属性
 * <p>
 * 目标是用于丰富实体数据中不具备任何逻辑特性的属性
 * <p>
 * 当扩展属性为枚举时，相关的值由系统存储
 *
 * @author zhanghan
 * @date 2021/8/25 15:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Deprecated
public abstract class BasicAttributeDefinition extends BasicEntity<String> {
    /**
     * 属性的名称
     * <p>
     * 注意，允许在某个作用域下具有同名属性
     */
    private String name;
    /**
     * 属性描述，用来在展示同名属性时进行补充说明
     * <p>
     * 比如两个属性都叫分辨率，则可以说明一个是电脑屏幕分辨率，一个是手机分辨率
     */
    private String description;
    /**
     * 属性提示，比如展示用途或一些简短的说明
     */
    private String hint;

    public BasicAttributeDefinition(Date createdTime, Date lastUpdateTime, String id, String name, String description, String hint) {
        super(createdTime, lastUpdateTime, id);
        this.name = name;
        this.description = description;
        this.hint = hint;
    }
}
