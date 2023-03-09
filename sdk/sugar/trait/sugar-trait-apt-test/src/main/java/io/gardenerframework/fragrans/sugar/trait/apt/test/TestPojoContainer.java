package io.gardenerframework.fragrans.sugar.trait.apt.test;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;
import io.gardenerframework.fragrans.sugar.trait.apt.other.OtherPackageClass;

import java.util.Date;

/**
 * @author zhanghan30
 * @date 2022/8/14 1:57 下午
 */
public class TestPojoContainer {
    /**
     * @author zhanghan30
     * @date 2022/8/14 2:58 上午
     */
    @Trait(namespace = TestPojoNamespace.class)
    private class TestPojo<T, C extends OtherPackageClass> {
        C otherPackageClass;
        private T id;
        private boolean b;
        private int test;
        private Date date;
    }

    /**
     * @author zhanghan30
     * @date 2022/8/14 1:40 下午
     */
    @Trait(namespace = TestPojoNamespace.class)
    private class TestPojo2 {
        private Long a;
    }
}
