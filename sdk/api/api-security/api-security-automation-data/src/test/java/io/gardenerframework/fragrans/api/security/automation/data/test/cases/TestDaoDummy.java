package io.gardenerframework.fragrans.api.security.automation.data.test.cases;

import io.gardenerframework.fragrans.api.security.automation.data.annotation.InjectOperator;
import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2023/2/3 13:19
 */
@Component
public class TestDaoDummy {
    public void testTypeInjection(String nothing, OperatorData data) {
        Assertions.assertNotNull(data.getOperator());
    }

    public void testAnnotationInjection(String noting, @InjectOperator String operator) {
        Assertions.assertNotNull(operator);
    }

    @Data
    public static class OperatorData implements SecurityTraits.AuditingTraits.IdentifierTraits.Operator {
        private String operator;
    }
}
