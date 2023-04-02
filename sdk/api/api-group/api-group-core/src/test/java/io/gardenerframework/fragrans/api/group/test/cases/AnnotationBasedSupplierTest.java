package io.gardenerframework.fragrans.api.group.test.cases;

import io.gardenerframework.fragrans.api.group.registry.ApiGroupRegistry;
import io.gardenerframework.fragrans.api.group.test.ApiGroupTestApplication;
import io.gardenerframework.fragrans.api.group.test.cases.utils.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/5/10 22:42
 */
@SpringBootTest(classes = ApiGroupTestApplication.class)
public class AnnotationBasedSupplierTest {
    @Autowired
    private ApiGroupRegistry registry;

    @Test
    public void smokeTest() {
        Assertions.assertTrue(Objects.requireNonNull(registry.getMember(TestAnnotation.class)).contains(TestBean.class));
        Assertions.assertTrue(Objects.requireNonNull(registry.getMember(TestAnnotation.class)).contains(TestBean2.class));
        Assertions.assertTrue(Objects.requireNonNull(registry.getMember(TestPolicy.class)).contains(TestBean.class));
        Assertions.assertNotNull(registry.getPolicy(TestBean.class, TestPolicy.class));
        Collection<Class<?>> member = registry.getMember(TestPolicy.class);
        //之前有个bug，两个策略提供同时提供同类型的策略时，后者会覆盖前者
        Assertions.assertTrue(member.contains(TestBean4.class));
        Assertions.assertTrue(member.contains(TestBean2.class));
    }
}
