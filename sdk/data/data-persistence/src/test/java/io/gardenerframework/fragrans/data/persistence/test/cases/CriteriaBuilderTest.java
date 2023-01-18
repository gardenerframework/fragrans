package io.gardenerframework.fragrans.data.persistence.test.cases;

import io.gardenerframework.fragrans.data.persistence.criteria.annotation.Equals;
import io.gardenerframework.fragrans.data.persistence.criteria.support.CriteriaBuilder;
import io.gardenerframework.fragrans.data.persistence.criteria.support.CriteriaBuilderStaticAccessor;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.DatabaseCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.EqualsCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import io.gardenerframework.fragrans.data.persistence.test.DataPersistenceTestApplication;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * @author ZhangHan
 * @date 2022/11/28 17:03
 */
@DisplayName("类扫描测试")
@SpringBootTest(classes = DataPersistenceTestApplication.class)
public class CriteriaBuilderTest {
    @Autowired
    private CriteriaBuilder criteriaBuilder;

    @Test
    public void smokeTest() {
        Map<Class<?>, DatabaseCriteria> test = criteriaBuilder.createCriteriaTraitMapping(
                null,
                TestEntity.class,
                new TestCriteria(),
                "test"
        );
        DatabaseCriteria basicCriteria = test.get(GenericTraits.IdentifierTraits.Id.class);
        //没有任何搜索值，条件被忽略
        Assertions.assertNull(basicCriteria);
        test = criteriaBuilder.createCriteriaTraitMapping(
                null,
                TestEntity.class,
                new TestCriteria(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                "test"
        );
        basicCriteria = test.get(GenericTraits.IdentifierTraits.Id.class);
        //给了值之后有了
        Assertions.assertTrue(basicCriteria instanceof EqualsCriteria);
        Assertions.assertEquals("`id` = #{test.id}", basicCriteria.build());
        basicCriteria = test.get(GenericTraits.LiteralTraits.Name.class);
        //默认判等
        Assertions.assertTrue(basicCriteria instanceof EqualsCriteria);
        Assertions.assertEquals("`name` = #{test.name}", basicCriteria.build());

        MatchAllCriteria criteria = CriteriaBuilderStaticAccessor.builder().createCriteria(
                "test",
                TestEntity.class,
                new TestCriteria(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                "test",
                Arrays.asList(GenericTraits.IdentifierTraits.Id.class, GenericTraits.LiteralTraits.Name.class),
                null
        );
        Assertions.assertEquals("((((`test`.`id` = #{test.id}) AND (`test`.`name` = #{test.name}))))", criteria.build());
        criteria = CriteriaBuilderStaticAccessor.builder().createCriteria(
                "test",
                TestEntity.class,
                new TestCriteria(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                "test",
                null,
                Arrays.asList(GenericTraits.IdentifierTraits.Id.class, GenericTraits.LiteralTraits.Name.class)
        );
        Assertions.assertEquals("((((`test`.`id` = #{test.id}) OR (`test`.`name` = #{test.name}))))", criteria.build());
        criteria = CriteriaBuilderStaticAccessor.builder().createCriteria(
                "test",
                ExtendCriteria.class,
                new TestCriteria(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                "test",
                null,
                Arrays.asList(GenericTraits.IdentifierTraits.Id.class, GenericTraits.LiteralTraits.Name.class)
        );
        Assertions.assertEquals("((((`test`.`id` = #{test.id}) OR (`test`.`name` = #{test.name}))))", criteria.build());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCriteria implements
            GenericTraits.IdentifierTraits.Id<String>,
            GenericTraits.LiteralTraits.Name {
        @Equals
        private String id;
        private String name;
    }

    @Getter
    @Setter
    public static class TestEntity implements
            GenericTraits.IdentifierTraits.Id<String>,
            GenericTraits.LiteralTraits.Name {
        private String id;
        private String name;
    }

    @NoArgsConstructor
    public class ExtendCriteria extends TestCriteria {
        public ExtendCriteria(String id, String name) {
            super(id, name);
        }
    }
}
