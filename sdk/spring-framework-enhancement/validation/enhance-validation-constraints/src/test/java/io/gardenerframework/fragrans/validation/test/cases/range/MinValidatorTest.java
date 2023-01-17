package io.gardenerframework.fragrans.validation.test.cases.range;

import io.gardenerframework.fragrans.validation.constraints.range.Min;
import io.gardenerframework.fragrans.validation.constraints.range.MinConstraintProvider;
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
@DisplayName("Max 注解测试")
@Import(value = {MinValidatorTest.MinProviderForSubClass.class, MinValidatorTest.MinProviderForSuperClass.class})
public class MinValidatorTest {
    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Test
    public void testMaxValidator() {
        TestBase testObject = new TestObject();
        testObject.setField(0L);
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField(11L);
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject = new TestSubClass();
        testObject.setField(10L);
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField(20L);
        Assertions.assertEquals(0, validator.validate(testObject).size());
    }

    public interface TestBase {
        long getField();

        void setField(long field);
    }

    @Data
    public static class TestObject implements TestBase {
        @Min(provider = MinProviderForSuperClass.class)
        private long field;
    }

    @Data
    public static class TestSubClass implements TestBase {
        @Min(provider = MinProviderForSubClass.class)
        private long field;
    }

    //fixme 当覆盖了父类属性时，父类的验证器依然会生效
    //这时需要将类型封装为包装类型
    @Component
    public static class MinProviderForSuperClass implements MinConstraintProvider {
        @Override
        public Number getMin() {
            return 10;
        }
    }

    @Component
    public static class MinProviderForSubClass implements MinConstraintProvider {
        @Override
        public Number getMin() {
            return 20;
        }
    }
}