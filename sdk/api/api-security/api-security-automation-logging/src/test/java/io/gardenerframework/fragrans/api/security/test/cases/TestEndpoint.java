package io.gardenerframework.fragrans.api.security.test.cases;

import io.gardenerframework.fragrans.api.security.operator.schema.OperatorBrief;
import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Process;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/6/14 2:47 下午
 */
@RestController
@RequestMapping("/")
@Slf4j
@AllArgsConstructor
@Component
public class TestEndpoint {
    private final GenericOperationLogger logger = GenericLoggers.operationLogger();
    private final OperatorBrief operator;

    @GetMapping
    public void test() {
        operator.setClientId(UUID.randomUUID().toString());
        operator.setUserId(UUID.randomUUID().toString());
        logger.info(log, GenericOperationLogContent.builder()
                        .what(TestEndpoint.class)
                        .operation(new Process())
                        .state(new Done()).build(),
                null
        );
    }
}
