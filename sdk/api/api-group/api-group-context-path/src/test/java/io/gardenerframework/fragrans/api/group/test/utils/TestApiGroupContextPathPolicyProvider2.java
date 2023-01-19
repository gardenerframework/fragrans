package io.gardenerframework.fragrans.api.group.test.utils;

import io.gardenerframework.fragrans.api.group.ApiGroupProvider;
import io.gardenerframework.fragrans.api.group.policy.ApiGroupContextPathPolicy;
import io.gardenerframework.fragrans.api.group.policy.ApiGroupPolicyProvider;
import io.gardenerframework.fragrans.api.group.test.endpoints.TestAnnotation2;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author zhanghan30
 * @date 2022/6/24 6:15 下午
 */
@Component
public class TestApiGroupContextPathPolicyProvider2 implements ApiGroupPolicyProvider<ApiGroupContextPathPolicy>, ApiGroupProvider {
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return TestAnnotation2.class;
    }

    @Override
    public ApiGroupContextPathPolicy getPolicy() {
        return new ApiGroupContextPathPolicy("/context-path2");
    }
}
