package io.gardenerframework.fragrans.api.security.automation.data.test.cases;

import io.gardenerframework.fragrans.api.security.operator.schema.OperatorBrief;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/3 13:16
 */
@RestController
@Component
@AllArgsConstructor
@RequestMapping("/")
public class TestEndpoint {
    private final OperatorBrief operatorBrief;
    private final TestDaoDummy testDaoDummy;

    @GetMapping
    public void test() {
        operatorBrief.setUserId(UUID.randomUUID().toString());
        testDaoDummy.testTypeInjection(UUID.randomUUID().toString(), new TestDaoDummy.OperatorData());
        testDaoDummy.testAnnotationInjection(UUID.randomUUID().toString(), null);
    }
}
