package io.gardenerframework.fragrans.validation.test.cases.text;

import io.gardenerframework.fragrans.validation.constraints.text.OptionalNonBlank;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Payload;
import javax.validation.Validator;

/**
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.1.0
 */
@SpringBootTest
@DisplayName("OptionalNonBlank 注解测试")
public class OptionalNonBlankValidatorTest {
    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Test
    @DisplayName("冒烟测试")
    public void testNullOrNotBlankValidator() {
        TestObject testObject = new TestObject();
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField(" ");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField("   ");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField("1");
        Assertions.assertEquals(0, validator.validate(testObject).size());
    }

    @Data
    public static class TestObject {
        @OptionalNonBlank(payload = TestPayload.class)
        private String field;
    }

    public static class TestPayload implements Payload {

    }
}
