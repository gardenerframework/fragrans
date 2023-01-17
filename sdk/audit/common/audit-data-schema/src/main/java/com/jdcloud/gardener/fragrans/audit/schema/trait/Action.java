package com.jdcloud.gardener.fragrans.audit.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2023/1/6 17:43
 */
@Trait
public interface Action {
    /**
     * 行为编码
     */
    String action = "";
}
