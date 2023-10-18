package io.gardenerframework.fragrans.log.test.cases;

import io.gardenerframework.fragrans.log.BasicLogger;
import io.gardenerframework.fragrans.log.test.LogEngineTestApplication;
import io.gardenerframework.fragrans.log.test.log.SubclassLogger;
import io.gardenerframework.fragrans.log.test.log.schema.TestTemplate;
import io.gardenerframework.fragrans.log.test.log.schema.TestWord;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2022/6/8 3:10 下午
 */
@SpringBootTest(classes = LogEngineTestApplication.class)
@Slf4j
public class BasicLoggerTest {
    @Autowired
    private BasicLogger basicLogger;
    @Autowired
    private SubclassLogger subclassLogger;

    @Test
    public void smokeTest() {
        basicLogger.debug(
                log,
                new TestTemplate(),
                Collections.singletonList(new TestWord()),
                null
        );
        basicLogger.debug(
                log,
                new TestTemplate(),
                Collections.singletonList(new TestWord()),
                new RuntimeException("test ex")
        );
        basicLogger.debug(
                log,
                new TestTemplate(),
                Collections.emptyList(),
                null
        );
        basicLogger.debug(
                log,
                new TestTemplate(),
                Collections.emptyList(),
                new RuntimeException("test ex")
        );
    }

}
