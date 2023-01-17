package com.jdcloud.gardener.fragrans.event.test.cases;

import com.jdcloud.gardener.fragrans.event.DomainEventHelper;
import com.jdcloud.gardener.fragrans.event.Topic;
import com.jdcloud.gardener.fragrans.event.test.KafkaDomainEventTestApplication;
import com.jdcloud.gardener.fragrans.schema.entity.BasicEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

/**
 * @author zhanghan30
 * @date 2021/11/4 10:43 下午
 */
@SpringBootTest(classes = KafkaDomainEventTestApplication.class)
@EmbeddedKafka(topics = "any-topic", bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@Import({KafkaDomainEventTest.Consumer.class})
public class KafkaDomainEventTest {
    private static final Logger logger = LoggerFactory.getLogger(KafkaDomainEventTest.class);
    private static final Semaphore SEMAPHORE = new Semaphore(0);
    public static boolean consumed = false;

    @Test
    @DisplayName("kafka领域事件冒烟测试")
    public void simpleSmokeTest() throws InterruptedException {
        KafkaDomainEventTest.consumed = false;
        DomainEventHelper.sendCreateRecordEvent(new TestEvent());
        SEMAPHORE.acquire();
        Assertions.assertTrue(KafkaDomainEventTest.consumed);
    }

    @Topic("any-topic")
    public static class TestEvent extends BasicEntity<String> {

    }

    @Component
    public static class Consumer {
        @Async
        @KafkaListener(topics = "any-topic", groupId = "test")
        public void consume(String event) {
            logger.info("消费: {}", event);
            KafkaDomainEventTest.consumed = true;
            SEMAPHORE.release();
        }
    }
}
