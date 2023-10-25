package io.gardenerframework.fragrans.data.persistence.test.cases;

import io.gardenerframework.fragrans.data.persistence.criteria.annotation.Equals;
import io.gardenerframework.fragrans.data.persistence.criteria.support.CriteriaBuilder;
import io.gardenerframework.fragrans.data.persistence.orm.database.Database;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.DatabaseCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.EqualsCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import io.gardenerframework.fragrans.data.persistence.test.DataPersistenceTestApplication;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import io.gardenerframework.fragrans.sugar.trait.utils.TraitUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * @author ZhangHan
 * @date 2022/11/28 17:03
 */
@SpringBootTest(classes = DataPersistenceTestApplication.class)
@ActiveProfiles("mysql")
public class CriteriaBuilderTest {
    private final CriteriaBuilder criteriaBuilder = CriteriaBuilder.getInstance();

    @BeforeEach
    public void ensureDriver() {
        Database.setDriver(DatabaseDriver.MYSQL);
    }

    @Test
    public void smokeTest() {
        Map<String, DatabaseCriteria> test = criteriaBuilder.createFieldCriteriaMapping(
                null,
                TestEntity.class,
                new TestCriteria(),
                "test"
        );
        DatabaseCriteria basicCriteria = test.get(TraitUtils.getTraitFieldNames(GenericTraits.IdentifierTraits.Id.class).stream().findFirst().get());
        //没有任何搜索值，条件被忽略
        Assertions.assertNull(basicCriteria);
        test = criteriaBuilder.createFieldCriteriaMapping(
                null,
                TestEntity.class,
                new TestCriteria(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                "test"
        );
        basicCriteria = test.get(TraitUtils.getTraitFieldNames(GenericTraits.IdentifierTraits.Id.class).stream().findFirst().get());
        //给了值之后有了
        Assertions.assertTrue(basicCriteria instanceof EqualsCriteria);
        Assertions.assertEquals("`id` = #{test.id}", basicCriteria.build());
        basicCriteria = test.get(TraitUtils.getTraitFieldNames(GenericTraits.LiteralTraits.Name.class).stream().findFirst().get());
        //默认判等
        Assertions.assertTrue(basicCriteria instanceof EqualsCriteria);
        Assertions.assertEquals("`name` = #{test.name}", basicCriteria.build());

        MatchAllCriteria criteria = CriteriaBuilder.getInstance().createCriteria(
                "test",
                TestEntity.class,
                new TestCriteria(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                "test",
                Arrays.asList(TraitUtils.getTraitFieldNames(GenericTraits.IdentifierTraits.Id.class).stream().findFirst().get(), TraitUtils.getTraitFieldNames(GenericTraits.LiteralTraits.Name.class).stream().findFirst().get()),
                null
        );
        Assertions.assertEquals("((((`test`.`id` = #{test.id}) AND (`test`.`name` = #{test.name}))))", criteria.build());
        criteria = CriteriaBuilder.getInstance().createCriteria(
                "test",
                TestEntity.class,
                new TestCriteria(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                "test",
                null,
                Arrays.asList(TraitUtils.getTraitFieldNames(GenericTraits.IdentifierTraits.Id.class).stream().findFirst().get(), TraitUtils.getTraitFieldNames(GenericTraits.LiteralTraits.Name.class).stream().findFirst().get())
        );
        Assertions.assertEquals("((((`test`.`id` = #{test.id}) OR (`test`.`name` = #{test.name}))))", criteria.build());
        criteria = CriteriaBuilder.getInstance().createCriteria(
                "test",
                ExtendCriteria.class,
                new TestCriteria(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                "test",
                null,
                Arrays.asList(TraitUtils.getTraitFieldNames(GenericTraits.IdentifierTraits.Id.class).stream().findFirst().get(), TraitUtils.getTraitFieldNames(GenericTraits.LiteralTraits.Name.class).stream().findFirst().get())
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
