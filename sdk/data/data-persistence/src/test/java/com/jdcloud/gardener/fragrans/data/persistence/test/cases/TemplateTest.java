package com.jdcloud.gardener.fragrans.data.persistence.test.cases;

import com.jdcloud.gardener.fragrans.data.persistence.template.support.DomainDaoTemplateRegistry;
import com.jdcloud.gardener.fragrans.data.persistence.template.support.DomainDaoTemplateTypesResolver;
import com.jdcloud.gardener.fragrans.data.persistence.template.support.DomainObjectTemplateTypesResolver;
import com.jdcloud.gardener.fragrans.data.persistence.test.DataPersistenceTestApplication;
import com.jdcloud.gardener.fragrans.data.persistence.test.utils.template.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/10/31 13:18
 */
@DisplayName("操作模板测试")
@SpringBootTest(classes = DataPersistenceTestApplication.class)
@MapperScan(basePackageClasses = TestRecordMapperTemplate.class)
public class TemplateTest {
    @Test
    @DisplayName("对象模板冒烟测试")
    public void smokeTest() {
        Collection<Class<?>> templateTypes = DomainObjectTemplateTypesResolver.resolveTemplateTypes(TestRecordMapperTemplate.class);
        Assertions.assertNotNull(templateTypes);
        Assertions.assertTrue(templateTypes.contains(TestRecordTemplate.class));
        Map<Class<?>, Class<?>> templateImplementationTypeMappings = DomainObjectTemplateTypesResolver.resolveTemplateImplementationTypeMappings(TestRecordMapperSubClass.class, TestRecordMapperTemplate.class);
        Assertions.assertNotNull(templateImplementationTypeMappings);
        Assertions.assertTrue(templateImplementationTypeMappings.containsKey(TestRecordTemplate.class));
        Assertions.assertNotNull(templateImplementationTypeMappings.get(TestRecordTemplate.class));
        Assertions.assertEquals(TestRecordTemplate.class, templateImplementationTypeMappings.get(TestRecordTemplate.class));
        templateImplementationTypeMappings = DomainObjectTemplateTypesResolver.resolveTemplateImplementationTypeMappings(TestRecordMapperSubClassWithTypesSubClass.class, TestRecordMapperTemplate.class);
        Assertions.assertNotNull(templateImplementationTypeMappings);
        Assertions.assertTrue(templateImplementationTypeMappings.containsKey(TestRecordTemplate.class));
        Assertions.assertNotNull(templateImplementationTypeMappings.get(TestRecordTemplate.class));
        Assertions.assertEquals(TestRecordTemplate.class, templateImplementationTypeMappings.get(TestRecordTemplate.class));
        templateImplementationTypeMappings = DomainObjectTemplateTypesResolver.resolveTemplateImplementationTypeMappings(TestRecordMapperSubClassWithTypesSubClass.class, TestRecordMapperSubClassWithTypes.class);
        Assertions.assertNotNull(templateImplementationTypeMappings);
        Assertions.assertFalse(templateImplementationTypeMappings.containsKey(TestRecordTemplate.class));
        Assertions.assertNull(templateImplementationTypeMappings.get(TestRecordTemplate.class));
        Assertions.assertEquals(SecondTemplate.class, templateImplementationTypeMappings.get(SecondTemplate.class));
    }

    @Test
    @DisplayName("dao模板冒烟测试")
    public void daoTemplateSomeTest() {
        Collection<Class<?>> templateTypes = DomainDaoTemplateTypesResolver.resolveTemplateTypes(
                TestRecordMapperTemplate.class
        );
        Assertions.assertEquals(1, templateTypes.size());
        Assertions.assertTrue(templateTypes.contains(TestRecordMapperTemplate.class));
        templateTypes = DomainDaoTemplateTypesResolver.resolveTemplateTypes(
                TestRecordMapperSubClassWithTypesSubClass.class
        );
        Assertions.assertEquals(2, templateTypes.size());
        Assertions.assertTrue(templateTypes.containsAll(Arrays.asList(TestRecordMapperTemplate.class, TestRecordMapperSubClassWithTypes.class)));

        DomainDaoTemplateRegistry.Item item = DomainDaoTemplateRegistry.getItem(TestRecordMapperTemplate.class);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(4, item.getImplementations().size());
        Assertions.assertTrue(item.getImplementations().containsAll(Arrays.asList(TestRecordMapperTemplate.class, TestRecordMapperSubClassWithTypes.class, TestRecordMapperSubClass.class, TestRecordMapperSubClassWithTypes.class)));
        item = DomainDaoTemplateRegistry.getItem(TestRecordMapperSubClassWithTypes.class);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(2, item.getImplementations().size());
        Assertions.assertTrue(item.getImplementations().containsAll(Arrays.asList(TestRecordMapperSubClassWithTypes.class, TestRecordMapperSubClassWithTypesSubClass.class)));
        Assertions.assertEquals(TestRecordMapperSubClassWithTypesSubClass.class, item.getActiveImplementation());

    }
}
