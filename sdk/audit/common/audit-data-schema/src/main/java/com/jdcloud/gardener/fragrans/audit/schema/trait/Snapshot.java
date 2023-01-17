package com.jdcloud.gardener.fragrans.audit.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2023/1/6 18:13
 */
@Trait
public interface Snapshot<S> {
    /**
     * 快照
     */
    S snapshot = null;
}
