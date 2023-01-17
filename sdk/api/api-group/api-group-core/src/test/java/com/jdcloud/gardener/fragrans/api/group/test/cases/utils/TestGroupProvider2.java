package com.jdcloud.gardener.fragrans.api.group.test.cases.utils;

import com.jdcloud.gardener.fragrans.api.group.ApiGroupProvider;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author zhanghan30
 * @date 2022/6/24 3:47 下午
 */
@Component
public class TestGroupProvider2 implements ApiGroupProvider {
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return TestAnnotation2.class;
    }
}
