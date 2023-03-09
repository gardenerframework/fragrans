package com.jdcloud.gardener.fragrans.event.schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2021/10/27 9:07 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class EntityFieldChangedMessageTemplate<F> extends UpdateMessageTemplate<F> {
    /**
     * 记录id
     */
    private String id;
    /**
     * 那个属性
     */
    private String which;
}
