package io.gardenerframework.fragrans.log.test.cases;

import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.annotation.LogTarget;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.OperatorDetail;
import io.gardenerframework.fragrans.log.test.GenericLogTestApplication;
import io.gardenerframework.fragrans.log.test.log.schema.TestDetail;
import io.gardenerframework.fragrans.log.test.log.schema.TestHow;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/6/9 2:14 下午
 */
@SpringBootTest(classes = GenericLogTestApplication.class)
@Slf4j
@LogTarget("通用日志测试")
public class GenericLogTest {
    @Autowired
    private GenericBasicLogger genericBasicLogger;
    @Autowired
    private GenericOperationLogger genericOperationLogger;

    @Test
    public void simpleSmokeTest() {
        GenericOperationLogger.enableLogEvent(log, true);
        genericBasicLogger.info(
                log,
                GenericBasicLogContent.builder()
                        .what(GenericLogTest.class)
                        .how(new TestHow())
                        .detail(new TestDetail())
                        .build(),
                null
        );
        genericOperationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(GenericLogTest.class)
                        .operation(new TestHow())
                        .state(new TestHow())
                        .detail(new TestDetail())
                        .operator(new OperatorDetail(UUID.randomUUID().toString(), null))
                        .build(),
                null
        );
        genericBasicLogger.info(
                log,
                GenericBasicLogContent.builder()
                        .what(GenericLogTest.class)
                        .how(new TestHow())
                        .build(),
                null
        );
        genericOperationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(GenericLogTest.class)
                        .operation(new TestHow())
                        .state(new TestHow())
                        .build(),
                null
        );
    }
}
