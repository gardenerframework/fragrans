package com.jdcloud.gardener.fragrans.validation.test.cases.text;

import com.jdcloud.gardener.fragrans.validation.constraints.text.LowerUnderscore;
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
@DisplayName("LowerUnderscore测试")
public class LowerUnderscoreValidatorTest {
    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Test
    @DisplayName("冒烟测试")
    public void testLowerUnderscoreValidator() {
        TestObject testObject = new TestObject();
        //null是ok的
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField("a");
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField("1ab");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField("_abv");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField("_2");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField("a_");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField("a_1");
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField("a_b");
        Assertions.assertEquals(0, validator.validate(testObject).size());
    }

    @Data
    public static class TestObject {
        @LowerUnderscore
        private String field;
    }
}
