package com.jdcloud.gardener.fragrans.event.schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * @author zhanghan30
 * @date 2021/10/27 9:07 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class RelationFieldChangedMessageTemplate<F> extends UpdateMessageTemplate<F> {
    /**
     * 关系id
     */
    private Map<String, String> relation;
    /**
     * 那个属性
     */
    private String which;
}
