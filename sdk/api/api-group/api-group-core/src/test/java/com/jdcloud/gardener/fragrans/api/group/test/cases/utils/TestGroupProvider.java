package com.jdcloud.gardener.fragrans.api.group.test.cases.utils;

import com.jdcloud.gardener.fragrans.api.group.ApiGroupProvider;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2022/6/24 3:47 下午
 */
@Component
public class TestGroupProvider implements ApiGroupProvider {
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return TestAnnotation.class;
    }

    @Nullable
    @Override
    public Collection<Class<?>> getAdditionalMembers() {
        return Collections.singleton(TestBean3.class);
    }
}
