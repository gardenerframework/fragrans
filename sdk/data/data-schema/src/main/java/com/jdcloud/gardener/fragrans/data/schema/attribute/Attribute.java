package com.jdcloud.gardener.fragrans.data.schema.attribute;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 聚合属性定义和值的属性对象
 *
 * @author zhanghan
 * @date 2021/8/27 11:17
 */
@Data
@SuperBuilder
@NoArgsConstructor
@Deprecated
public class Attribute<A extends BasicAttributeDefinition, V extends BasicAttributeValue> {
    A definition;
    V value;

    public Attribute(A definition, V value) {
        this.definition = definition;
        this.value = value;
    }
}
