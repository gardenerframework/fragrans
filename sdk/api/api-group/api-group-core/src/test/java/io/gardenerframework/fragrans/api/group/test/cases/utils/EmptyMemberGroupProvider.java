package io.gardenerframework.fragrans.api.group.test.cases.utils;

import io.gardenerframework.fragrans.api.group.ApiGroupProvider;
import io.gardenerframework.fragrans.api.group.policy.ApiGroupPolicyProvider;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author zhanghan30
 * @date 2022/6/24 3:47 下午
 */
@Component
public class EmptyMemberGroupProvider implements ApiGroupProvider, ApiGroupPolicyProvider<EmptyMemberGroupPolicy> {
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return TestEmptyMember.class;
    }

    @Override
    public EmptyMemberGroupPolicy getPolicy() {
        return new EmptyMemberGroupPolicy();
    }
}
