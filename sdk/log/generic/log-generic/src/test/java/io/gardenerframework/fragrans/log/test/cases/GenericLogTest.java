package io.gardenerframework.fragrans.log.test.cases;

import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.test.GenericLogTestApplication;
import io.gardenerframework.fragrans.log.test.log.schema.TestDetail;
import io.gardenerframework.fragrans.log.test.log.schema.TestHow;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhanghan30
 * @date 2022/6/9 2:14 下午
 */
@SpringBootTest(classes = GenericLogTestApplication.class)
@Slf4j
public class GenericLogTest {

    @Test
    public void simpleSmokeTest() {
        GenericLoggers.basicLogger().info(
                log,
                GenericBasicLogContent.builder()
                        .what(GenericLogTest.class)
                        .how(new TestHow())
                        .detail(new TestDetail())
                        .build(),
                null
        );
        GenericLoggers.operationLogger().info(
                log,
                GenericOperationLogContent.builder()
                        .what(GenericLogTest.class)
                        .operation(new TestHow())
                        .state(new TestHow())
                        .detail(new TestDetail())
                        .build(),
                null
        );
        GenericLoggers.basicLogger().info(
                log,
                GenericBasicLogContent.builder()
                        .what(GenericLogTest.class)
                        .how(new TestHow())
                        .build(),
                null
        );
        GenericLoggers.operationLogger().info(
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
