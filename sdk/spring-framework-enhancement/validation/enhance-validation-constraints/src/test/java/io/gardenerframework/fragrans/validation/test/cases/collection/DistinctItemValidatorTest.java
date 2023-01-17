package io.gardenerframework.fragrans.validation.test.cases.collection;

import io.gardenerframework.fragrans.validation.constraints.collection.DistinctItem;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.Validator;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhanghan
 * @date 2020-11-07 12:46
 * @since 1.1.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DistinctItemValidatorTest {
    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }


    @Test
    public void somkeTest() {
        TestObject testObject = new TestObject();
        Assertions.assertEquals(0, validator.validate(testObject).size());
        testObject.setField(Arrays.asList("1", "1"));
        Assertions.assertEquals(1, validator.validate(testObject).size());
        testObject.setField(Arrays.asList("1", "2"));
        Assertions.assertEquals(0, validator.validate(testObject).size());
    }

    @Data
    public static class TestObject {
        @DistinctItem
        private List<String> field;
    }
}
