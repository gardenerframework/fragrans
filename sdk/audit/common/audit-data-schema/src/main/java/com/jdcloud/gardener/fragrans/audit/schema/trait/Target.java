package com.jdcloud.gardener.fragrans.audit.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2023/1/6 17:45
 */
@Trait
public interface Target {
    /**
     * 被操作的目标
     */
    String target = "";
}
