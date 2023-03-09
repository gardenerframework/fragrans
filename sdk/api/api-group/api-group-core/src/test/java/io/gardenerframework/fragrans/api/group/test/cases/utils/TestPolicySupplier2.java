package io.gardenerframework.fragrans.api.group.test.cases.utils;

import io.gardenerframework.fragrans.api.group.policy.ApiGroupPolicyProvider;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author ZhangHan
 * @date 2022/5/10 22:43
 */
@Component
public class TestPolicySupplier2 implements ApiGroupPolicyProvider<TestPolicy> {
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return TestAnnotation2.class;
    }

    @Override
    public TestPolicy getPolicy() {
        return new TestPolicy();
    }
}
