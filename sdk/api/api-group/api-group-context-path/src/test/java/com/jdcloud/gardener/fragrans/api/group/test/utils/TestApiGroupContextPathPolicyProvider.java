package com.jdcloud.gardener.fragrans.api.group.test.utils;

import com.jdcloud.gardener.fragrans.api.group.ApiGroupProvider;
import com.jdcloud.gardener.fragrans.api.group.policy.ApiGroupContextPathPolicy;
import com.jdcloud.gardener.fragrans.api.group.policy.ApiGroupPolicyProvider;
import com.jdcloud.gardener.fragrans.api.group.test.endpoints.TestAnnotation;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author zhanghan30
 * @date 2022/6/24 6:15 下午
 */
@Component
public class TestApiGroupContextPathPolicyProvider implements ApiGroupPolicyProvider<ApiGroupContextPathPolicy>, ApiGroupProvider {
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return TestAnnotation.class;
    }

    @Override
    public ApiGroupContextPathPolicy getPolicy() {
        return new ApiGroupContextPathPolicy("/context-path");
    }
}
