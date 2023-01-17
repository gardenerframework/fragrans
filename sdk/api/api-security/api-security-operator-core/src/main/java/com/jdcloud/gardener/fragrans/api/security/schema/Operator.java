package com.jdcloud.gardener.fragrans.api.security.schema;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2022/9/23 13:14
 */
@Trait
public interface Operator {
    /**
     * 用户id
     */
    String userId = "";
    /**
     * 客户端id
     */
    String clientId = "";
}
