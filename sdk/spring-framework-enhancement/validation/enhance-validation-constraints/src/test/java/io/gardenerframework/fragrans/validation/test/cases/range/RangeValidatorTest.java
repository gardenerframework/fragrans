package io.gardenerframework.fragrans.validation.test.cases.range;

import io.gardenerframework.fragrans.validation.constraints.range.MaxConstraintProvider;
import io.gardenerframework.fragrans.validation.constraints.range.MinConstraintProvider;
import io.gardenerframework.fragrans.validation.constraints.range.Range;
import io.gardenerframework.fragrans.validation.test.ValidationConstraitnsTestApplication;
import lombok.Data;
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
@DisplayName("Range 注解测试")
@Import(value = {RangeValidatorTest.MinProvider.class, RangeValidatorTest.MaxProvider.class})
public class RangeValidatorTest {
    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Test
    public void testMaxValidator() {
        TestObject testObject = new TestObject();
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField(11);
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField(21);
        Assertions.assertEquals(1, validator.validate(testObject).size());
    }

    @Data
    public static class TestObject {
        @Range(max = MaxProvider.class, min = MinProvider.class)
        private long field;
    }


    @Component
    public static class MaxProvider implements MaxConstraintProvider {
        @Override
        public Number getMax() {
            return 20;
        }
    }

    @Component
    public static class MinProvider implements MinConstraintProvider {
        @Override
        public Number getMin() {
            return 10;
        }
    }
}