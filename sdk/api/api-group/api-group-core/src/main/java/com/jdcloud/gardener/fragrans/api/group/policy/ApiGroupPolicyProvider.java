package com.jdcloud.gardener.fragrans.api.group.policy;

import java.lang.annotation.Annotation;

/**
 * @author zhanghan30
 * @date 2022/6/24 2:25 下午
 */
public interface ApiGroupPolicyProvider<P extends ApiGroupPolicy> {
    /**
     * 对应的分组注解
     *
     * @return 注解类
     */
    Class<? extends Annotation> getAnnotation();

    /**
     * 返回对应的组策略
     *
     * @return 策略
     */
    P getPolicy();
}
