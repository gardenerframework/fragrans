package com.jdcloud.gardener.fragrans.audit.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2023/1/6 17:47
 */
@Trait
public interface Response<R> {
    /**
     * 响应
     */
    R response = null;
}
