package io.gardenerframework.fragrans.data.practice.test.cases;

import io.gardenerframework.fragrans.data.practice.operation.CommonOperations;
import io.gardenerframework.fragrans.data.practice.operation.checker.BaseEntityActiveStatusChecker;
import io.gardenerframework.fragrans.data.practice.operation.checker.BaseEntityEnabledStatusChecker;
import io.gardenerframework.fragrans.data.practice.operation.checker.BaseEntityExistenceChecker;
import io.gardenerframework.fragrans.data.practice.operation.checker.BaseEntityNotLockedStatusChecker;
import io.gardenerframework.fragrans.data.practice.test.DataDomainCommonPracticeApplication;
import io.gardenerframework.fragrans.data.schema.entity.BasicEntity;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import io.gardenerframework.fragrans.log.annotation.LogTarget;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/6/17 1:09 下午
 */
@SpringBootTest(classes = DataDomainCommonPracticeApplication.class)
public class CheckerTest {
    @Autowired
    private CommonOperations commonOperations;

    @Test
    public void existenceCheckerSmokeTest() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () ->
                        commonOperations.readThenCheck().single(
                                () -> null,
                                TestObjectExistenceChecker.builder()
                                        .target(TestObject.class)
                                        .id(UUID.randomUUID().toString())
                                        .build()
                        )
        );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () ->
                        commonOperations.readThenCheck().collection(
                                () -> Collections.emptyList(),
                                TestObjectExistenceChecker.builder()
                                        .target(TestObject.class)
                                        .ids(Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                                        .build()
                        )
        );
    }

    @Test
    public void statusCheckerSmokeTest() {
        String id = UUID.randomUUID().toString();
        TestObject testObject = new TestObject();
        testObject.setId(id);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> commonOperations.readThenCheck().single(
                        () -> {
                            testObject.setEnabled(false);
                            return testObject;
                        },
                        TestObjectEnableChecker.<String, TestObject>builder()
                                .target(TestObject.class)
                                .ids(Collections.singleton(id))
                                .build()
                )
        );
        commonOperations.readThenCheck().single(
                () -> {
                    testObject.setEnabled(true);
                    return testObject;
                },
                TestObjectEnableChecker.<String, TestObject>builder()
                        .target(TestObject.class)
                        .ids(Collections.singleton(id))
                        .build()
        );
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> commonOperations.readThenCheck().single(
                        () -> {
                            testObject.setActive(false);
                            return testObject;
                        },
                        TestObjectActiveChecker.<String, TestObject>builder()
                                .target(TestObject.class)
                                .id(id)
                                .build()
                )
        );
        commonOperations.readThenCheck().single(
                () -> {
                    testObject.setActive(true);
                    return testObject;
                },
                TestObjectActiveChecker.<String, TestObject>builder()
                        .target(TestObject.class)
                        .id(id)
                        .build()
        );
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> commonOperations.readThenCheck().single(
                        () -> {
                            testObject.setLocked(true);
                            return testObject;
                        },
                        TestObjectNotLockedChecker.<String, TestObject>builder()
                                .target(TestObject.class)
                                .ids(Collections.singleton(id))
                                .build()
                )
        );
        commonOperations.readThenCheck().single(
                () -> {
                    testObject.setLocked(false);
                    return testObject;
                },
                TestObjectNotLockedChecker.<String, TestObject>builder()
                        .target(TestObject.class)
                        .ids(Collections.singleton(id))
                        .build()
        );
    }

    @LogTarget("测试用对象")
    @Getter
    @Setter
    public static class TestObject extends BasicEntity<String> implements
            GenericTraits.StatusTraits.EnableFlag,
            GenericTraits.StatusTraits.ActiveFlag, GenericTraits.StatusTraits.LockFlag {
        private boolean enabled;
        private boolean locked;
        private boolean active;
    }

    @SuperBuilder
    public static class TestObjectExistenceChecker extends BaseEntityExistenceChecker<String, TestObject> {
        @Override
        protected RuntimeException raiseException(Collection<String> invalidIds, Phase phase) {
            return new IllegalArgumentException();
        }
    }

    @SuperBuilder
    public static class TestObjectActiveChecker extends BaseEntityActiveStatusChecker<String, TestObject> {

        @Override
        protected RuntimeException raiseException(Collection<String> invalidIds, Phase phase) {
            return new IllegalArgumentException();
        }
    }

    @SuperBuilder
    public static class TestObjectEnableChecker extends BaseEntityEnabledStatusChecker<String, TestObject> {

        @Override
        protected RuntimeException raiseException(Collection<String> invalidIds, Phase phase) {
            return new IllegalArgumentException();
        }
    }

    @SuperBuilder
    public static class TestObjectNotLockedChecker extends BaseEntityNotLockedStatusChecker<String, TestObject> {

        @Override
        protected RuntimeException raiseException(Collection<String> invalidIds, Phase phase) {
            return new IllegalArgumentException();
        }
    }
}
