package com.jdcloud.gardener.fragrans.data.practice.test.cases;

import com.jdcloud.gardener.fragrans.data.practice.operation.CommonOperations;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.BasicEntityActiveStatusChecker;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.BasicEntityEnabledStatusChecker;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.BasicEntityExistenceChecker;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.BasicEntityNotLockedStatusChecker;
import com.jdcloud.gardener.fragrans.data.practice.test.DataDomainCommonPracticeApplication;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
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
                                        .recordIds(Collections.singleton(UUID.randomUUID().toString()))
                                        .exceptionFactory((ids, reason) -> new IllegalArgumentException())
                                        .build()
                        )
        );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () ->
                        commonOperations.readThenCheck().collection(
                                () -> Collections.emptyList(),
                                TestObjectExistenceChecker.builder()
                                        .recordIds(Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                                        .exceptionFactory((ids, reason) -> new IllegalArgumentException())
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
                        BasicEntityEnabledStatusChecker.<String, TestObject>builder()
                                .type(TestObject.class)
                                .recordIds(Collections.singleton(id))
                                .exceptionFactory((ids, reason) -> new IllegalArgumentException())
                                .build()
                )
        );
        commonOperations.readThenCheck().single(
                () -> {
                    testObject.setEnabled(true);
                    return testObject;
                },
                BasicEntityEnabledStatusChecker.<String, TestObject>builder()
                        .type(TestObject.class)
                        .recordIds(Collections.singleton(id))
                        .exceptionFactory((ids, reason) -> new IllegalArgumentException())
                        .build()
        );
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> commonOperations.readThenCheck().single(
                        () -> {
                            testObject.setActive(false);
                            return testObject;
                        },
                        BasicEntityActiveStatusChecker.<String, TestObject>builder()
                                .type(TestObject.class)
                                .recordIds(Collections.singleton(id))
                                .exceptionFactory((ids, reason) -> new IllegalArgumentException())
                                .build()
                )
        );
        commonOperations.readThenCheck().single(
                () -> {
                    testObject.setActive(true);
                    return testObject;
                },
                BasicEntityActiveStatusChecker.<String, TestObject>builder()
                        .type(TestObject.class)
                        .recordIds(Collections.singleton(id))
                        .exceptionFactory((ids, reason) -> new IllegalArgumentException())
                        .build()
        );
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> commonOperations.readThenCheck().single(
                        () -> {
                            testObject.setLocked(true);
                            return testObject;
                        },
                        BasicEntityNotLockedStatusChecker.<String, TestObject>builder()
                                .type(TestObject.class)
                                .recordIds(Collections.singleton(id))
                                .exceptionFactory((ids, reason) -> new IllegalArgumentException())
                                .build()
                )
        );
        commonOperations.readThenCheck().single(
                () -> {
                    testObject.setLocked(false);
                    return testObject;
                },
                BasicEntityNotLockedStatusChecker.<String, TestObject>builder()
                        .type(TestObject.class)
                        .recordIds(Collections.singleton(id))
                        .exceptionFactory((ids, reason) -> new IllegalArgumentException())
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
    public static class TestObjectExistenceChecker extends BasicEntityExistenceChecker<String, TestObject> {
    }
}
