package com.jdcloud.gardener.fragrans.event.standard.schema;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2022/9/23 13:48
 */
@Trait
public interface FieldChangedEvent<O> extends Event {
    String field = null;
}
