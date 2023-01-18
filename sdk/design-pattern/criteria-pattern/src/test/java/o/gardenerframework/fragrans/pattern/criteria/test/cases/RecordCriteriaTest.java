package o.gardenerframework.fragrans.pattern.criteria.test.cases;

import io.gardenerframework.fragrans.pattern.criteria.schema.persistence.RecordCriteria;
import io.gardenerframework.fragrans.pattern.criteria.schema.root.BaseBooleanCriteria;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhanghan30
 * @date 2023/1/17 17:32
 */
@SpringBootTest
public class RecordCriteriaTest {
    @Test
    public void smokeTest() {
        BooleanCriteria criteria = BooleanCriteria.builder()
                .a(new EqualsCriteria("1", "1"))
                .operator(BaseBooleanCriteria.Operator.OR)
                .b(new EqualsCriteria("2", "2"))
                .build();
        Assertions.assertEquals("1=1OR2=2", criteria.build());
        //内嵌也没有问题
        criteria.setA(criteria);
    }

    interface DatabaseRecordCriteria extends RecordCriteria<String> {

    }

    @AllArgsConstructor
    static class EqualsCriteria implements DatabaseRecordCriteria {
        private String column;
        private String value;

        @Override
        public String build() {
            return column + "=" + value;
        }
    }

    @SuperBuilder
    static class BooleanCriteria extends BaseBooleanCriteria<DatabaseRecordCriteria> implements DatabaseRecordCriteria {
        @Override
        public String build() {
            return getA().build() + getOperator() + getB().build();
        }
    }

}
