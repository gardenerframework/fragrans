package io.gardenerframework.fragrans.log.test.cases;

import io.gardenerframework.fragrans.log.BasicLogger;
import io.gardenerframework.fragrans.log.event.LogEvent;
import io.gardenerframework.fragrans.log.test.LogEngineTestApplication;
import io.gardenerframework.fragrans.log.test.log.SubclassLogger;
import io.gardenerframework.fragrans.log.test.log.schema.TestTemplate;
import io.gardenerframework.fragrans.log.test.log.schema.TestWord;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2022/6/8 3:10 下午
 */
@SpringBootTest(classes = LogEngineTestApplication.class)
@Slf4j
@Import(value = BasicLoggerTest.Listener.class)
public class BasicLoggerTest {
    @Autowired
    private BasicLogger basicLogger;
    @Autowired
    private SubclassLogger subclassLogger;
    @Autowired
    private Listener listener;

    private boolean eventReceived = false;

    @Test
    public void smokeTest() {
        listener.setTest(this);
        basicLogger.enableLogEvent(log, true);
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
        Assertions.assertTrue(eventReceived);
    }

    public static class Listener {
        @Setter
        private BasicLoggerTest test;

        @EventListener
        public void onLogEvent(LogEvent event) {
            test.eventReceived = true;
        }
    }

}
