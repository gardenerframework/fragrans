package com.jdcloud.gardener.fragrans.data.schema.attribute;

import com.jdcloud.gardener.fragrans.data.schema.annotation.ImmutableField;
import com.jdcloud.gardener.fragrans.data.schema.annotation.ImmutableRelation;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 存储的属性值
 * <p>
 * 任何属性和值之间都是链接关系
 *
 * @author zhanghan
 * @date 2021/8/25 18:11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Deprecated
public abstract class BasicAttributeValue extends BasicEntity<String> {
    /**
     * 所属的属性id
     */
    @ImmutableRelation
    private String attributeId;
    /**
     * 值
     */
    @ImmutableField
    private String value;

    public BasicAttributeValue(Date createdTime, Date lastUpdateTime, String id, String attributeId, String value) {
        super(createdTime, lastUpdateTime, id);
        this.attributeId = attributeId;
        this.value = value;
    }
}
