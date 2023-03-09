package io.gardenerframework.fragrans.sugar.trait.apt.test;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2022/8/14 4:39 下午
 */
public class TestPojoClassToInterfaceInNamespace {
    @Trait
    class TestPojo {
        private int test;
    }
}
