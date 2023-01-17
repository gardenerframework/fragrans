package o.gardenerframework.fragrans.pattern.criteria.test.cases;

import io.gardenerframework.fragrans.pattern.criteria.schema.object.JavaObjectCriteria;
import io.gardenerframework.fragrans.pattern.criteria.schema.object.MatchAllCriteria;
import io.gardenerframework.fragrans.pattern.criteria.schema.object.NotCriteria;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/1/17 17:32
 */
@SpringBootTest
public class JavaObjectCriteriaTest {
    @Test
    public void smokeTest() {
        Assertions.assertFalse(new ObjectHasItemCriteria().meetCriteria(TestObject.builder().build()));
        Assertions.assertTrue(new ObjectHasItemCriteria().meetCriteria(TestSunObject.builder().item(UUID.randomUUID().toString()).build()));
        MatchAllCriteria<TestSunObject> matchAllCriteria = MatchAllCriteria.<TestSunObject>builder()
                .criteria(new ObjectHasItemCriteria()).build();
        Assertions.assertTrue(matchAllCriteria.meetCriteria(TestSunObject.builder().item(UUID.randomUUID().toString()).build()));
        NotCriteria<TestSunObject> notCriteria = NotCriteria.<TestSunObject>builder().criteria(new ObjectHasItemCriteria()).build();
        Assertions.assertFalse(notCriteria.meetCriteria(TestSunObject.builder().item(UUID.randomUUID().toString()).build()));
    }

    @Getter
    @Setter
    @SuperBuilder
    static class TestObject {
        private String item;
    }

    @Getter
    @Setter
    @SuperBuilder
    static class TestSunObject extends TestObject {
        private String item2;
    }

    static class ObjectHasItemCriteria implements JavaObjectCriteria<TestObject> {

        @Override
        public boolean meetCriteria(TestObject object) {
            return object.getItem() != null;
        }
    }
}
