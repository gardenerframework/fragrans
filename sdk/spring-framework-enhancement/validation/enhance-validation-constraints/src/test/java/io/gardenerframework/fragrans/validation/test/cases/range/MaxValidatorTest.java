package io.gardenerframework.fragrans.validation.test.cases.range;

import io.gardenerframework.fragrans.validation.constraints.range.Max;
import io.gardenerframework.fragrans.validation.constraints.range.MaxConstraintProvider;
import io.gardenerframework.fragrans.validation.test.ValidationConstraitnsTestApplication;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import javax.validation.Validator;

/**
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.1.0
 */
@SpringBootTest(classes = ValidationConstraitnsTestApplication.class)
@DisplayName("Max 注解测试")
@Import(value = {MaxValidatorTest.MaxProviderForSubClass.class, MaxValidatorTest.MaxProviderForSuperClass.class})
public class MaxValidatorTest {
    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Test
    public void testMaxValidator() {
        TestObject testObject = new TestObject();
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField(11);
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject = new TestSubClass();
        testObject.setField(10);
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField(11);
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField(21);
        Assertions.assertEquals(1, validator.validate(testObject).size());
    }

    public interface TestBase {
        long getField();
    }

    @Data
    public static class TestObject implements TestBase {
        @Max(provider = MaxProviderForSuperClass.class)
        private long field;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class TestSubClass extends TestObject {
        @Max(provider = MaxProviderForSubClass.class)
        private long field;
    }

    @Component
    public static class MaxProviderForSuperClass implements MaxConstraintProvider {
        @Override
        public Number getMax() {
            return 10;
        }
    }

    @Component
    public static class MaxProviderForSubClass implements MaxConstraintProvider {
        @Override
        public Number getMax() {
            return 20;
        }
    }
}