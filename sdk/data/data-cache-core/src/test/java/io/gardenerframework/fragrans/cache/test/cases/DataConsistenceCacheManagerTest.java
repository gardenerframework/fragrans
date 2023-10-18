package io.gardenerframework.fragrans.cache.test.cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.fragrans.cache.test.CacheCoreTestApplication;
import io.gardenerframework.fragrans.cache.test.NoScriptingTestRedisClient;
import io.gardenerframework.fragrans.cache.test.TestRedisClient;
import io.gardenerframework.fragrans.data.cache.lock.context.ThreadLocalLockContextHolder;
import io.gardenerframework.fragrans.data.cache.manager.DataConsistenceCacheManager;
import io.gardenerframework.fragrans.data.cache.manager.annotation.Cached;
import io.gardenerframework.fragrans.log.GenericLoggers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.TaskScheduler;

import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.UUID;

/**
 * @author ZhangHan
 * @date 2022/6/18 1:33
 */
@SpringBootTest(classes = CacheCoreTestApplication.class)
@DisplayName("数据一致性缓存管理器测试")
public class DataConsistenceCacheManagerTest {
    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private TaskScheduler taskScheduler;

    @Test
    public void smokeTest() throws IOException, InterruptedException {
        DataConsistenceCacheManager<TestObject> manager = new DataConsistenceCacheManager<TestObject>(new TestRedisClient(connectionFactory), new ThreadLocalLockContextHolder(), taskScheduler, (id) -> null) {{
            this.setLoggingMethod(GenericLoggers.operationLogger()::error);
        }};
        manager.setDataConsistenceTaskDelay(Duration.ofSeconds(10));
        TestObject testObject = new TestObject(UUID.randomUUID().toString());
        manager.set("test", testObject, null);
        TestObject test = manager.get("test");
        Assertions.assertNotNull(test);
        Assertions.assertEquals(test, testObject);
        String digest = manager.getDigest("test");
        Assertions.assertNotNull(digest);
        testObject.setId(UUID.randomUUID().toString());
        Assertions.assertTrue(manager.setIfPresents("test", testObject, null));
        test = manager.get("test");
        Assertions.assertNotNull(test);
        Assertions.assertEquals(test, testObject);
        manager.delete("test");
        testObject.setId(UUID.randomUUID().toString());
        Assertions.assertFalse(manager.setIfPresents("test", testObject, null));
        Assertions.assertTrue(manager.setIfNotPresents("test", testObject, null));
        test = manager.get("test");
        Assertions.assertNotNull(test);
        Assertions.assertEquals(test, testObject);
        Thread.sleep(12 * 1000);
        //1分钟后检查
        Assertions.assertNull(manager.get("test"));
    }

    @Test
    public void smokeTestWithNoScripting() throws IOException, InterruptedException {
        String primaryId = UUID.randomUUID().toString();
        TestObject testObject = new TestObject(primaryId);
        //默认总是返回最开始的对象
        DataConsistenceCacheManager<TestObject> manager = new DataConsistenceCacheManager<TestObject>(new NoScriptingTestRedisClient(connectionFactory), new ThreadLocalLockContextHolder(), new ObjectMapper(), taskScheduler, (id) -> new TestObject(primaryId)) {{
            this.setLoggingMethod(GenericLoggers.operationLogger()::error);
        }};
        manager.setDataConsistenceTaskDelay(Duration.ofSeconds(10));
        manager.set("test", testObject, null);
        TestObject test = manager.get("test");
        Assertions.assertNotNull(test);
        Assertions.assertEquals(test, testObject);
        testObject.setId(UUID.randomUUID().toString());
        //这里摘要key理应被覆盖
        Assertions.assertTrue(manager.setIfPresents("test", testObject, null));
        test = manager.get("test");
        Assertions.assertNotNull(test);
        Assertions.assertEquals(test, testObject);
        //查看摘要key是否被覆盖
        Thread.sleep(12 * 1000);
        //检查是否已经删除
        Assertions.assertNull(manager.get("test"));
        manager.delete("test");
        testObject.setId(UUID.randomUUID().toString());
        Assertions.assertFalse(manager.setIfPresents("test", testObject, null));
        Assertions.assertTrue(manager.setIfNotPresents("test", testObject, null));
        test = manager.get("test");
        Assertions.assertNotNull(test);
        Assertions.assertEquals(test, testObject);
        Thread.sleep(12 * 1000);
        //检查是否已经删除
        //因为源的摘要还是不变的
        Assertions.assertNull(manager.get("test"));
    }

    @Cached(namespaces = {"test"}, suffix = "test")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class TestObject implements Serializable {
        private String id;
    }
}
