package com.jdcloud.gardener.fragrans.api.security.test.cases;

import com.jdcloud.gardener.fragrans.api.security.log.schema.Subject;
import com.jdcloud.gardener.fragrans.api.security.schema.Operator;
import com.jdcloud.gardener.fragrans.log.GenericOperationLogger;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Process;
import com.jdcloud.gardener.fragrans.log.event.LogEvent;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.context.event.EventListener;
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
    private final GenericOperationLogger logger;
    private final Operator operator;

    @GetMapping
    public void test() {
        operator.setClientId(UUID.randomUUID().toString());
        operator.setUserId(UUID.randomUUID().toString());
        logger.enableLogEvent(log, true);
        logger.info(log, GenericOperationLogContent.builder()
                        .what(TestEndpoint.class)
                        .operation(new Process())
                        .state(new Done()).build(),
                null
        );
    }

    @EventListener
    public void onLogging(LogEvent event) {
        GenericOperationLogContent content = logger.unwrapContent(event);
        Assertions.assertNotNull(content);
        Assertions.assertNotNull(content.getOperator());
        Assertions.assertTrue(content.getOperator().getUser() instanceof Subject);
        Assertions.assertTrue(content.getOperator().getClient() instanceof Subject);
    }
}
