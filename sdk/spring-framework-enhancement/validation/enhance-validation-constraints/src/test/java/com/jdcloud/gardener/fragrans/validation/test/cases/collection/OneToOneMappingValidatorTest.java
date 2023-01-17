package com.jdcloud.gardener.fragrans.validation.test.cases.collection;

import com.jdcloud.gardener.fragrans.validation.constraints.map.OneToOneMapping;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.1.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OneToOneMappingValidatorTest {
    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Test
    public void testOneToOneMappingValidator() {
        TestObject testObject = new TestObject();
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField(new HashMap<>());
        testObject.getField().put("1", "1");
        testObject.getField().put("2", "1");
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.getField().clear();
        testObject.getField().put("1", "1");
        testObject.getField().put("2", null);
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.getField().clear();
        testObject.getField().put("1", "1");
        testObject.getField().put("2", "2");
        Assertions.assertEquals(0, validator.validate(testObject).size());

    }

    @Data
    public static class TestObject {
        @OneToOneMapping
        private Map<String, String> field;
    }
}
