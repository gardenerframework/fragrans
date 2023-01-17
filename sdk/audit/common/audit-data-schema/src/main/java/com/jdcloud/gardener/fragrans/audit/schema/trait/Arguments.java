package com.jdcloud.gardener.fragrans.audit.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2023/1/6 17:46
 */
@Trait
public interface Arguments<A> {
    /**
     * 入参
     */
    A arguments = null;
}
