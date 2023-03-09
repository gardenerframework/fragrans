package io.gardenerframework.fragrans.validation.test.cases.text;

import io.gardenerframework.fragrans.validation.constraints.text.UpperUnderscore;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.Validator;

/**
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.1.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("UpperUnderscore测试")
public class UpperUnderscoreValidatorTest {
    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Test
    @DisplayName("冒烟测试")
    public void testUpperUnderscoreValidator() {
        TestObject testObject = new TestObject();
        //null是ok的
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField("A");
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField("1AB");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField("_ABC");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField("_2");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField("A_");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField("A_1");
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField("A_B");
    }

    @Data
    public static class TestObject {
        @UpperUnderscore
        private String field;
    }
}
