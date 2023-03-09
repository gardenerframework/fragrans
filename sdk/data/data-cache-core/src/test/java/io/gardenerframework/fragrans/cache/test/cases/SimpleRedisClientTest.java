package io.gardenerframework.fragrans.cache.test.cases;

import io.gardenerframework.fragrans.cache.test.CacheCoreTestApplication;
import io.gardenerframework.fragrans.cache.test.TestRedisClient;
import io.gardenerframework.fragrans.data.cache.serialize.LongSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/3/30 4:30 下午
 */
@SpringBootTest(classes = CacheCoreTestApplication.class)
public class SimpleRedisClientTest {
    @Autowired
    private RedisConnectionFactory connectionFactory;

    @DisplayName("测试脚本运行")
    @Test
    public void testRedisScript() throws IOException {
        TestRedisClient redisClient = new TestRedisClient(connectionFactory);
        Assertions.assertNotNull(redisClient.loadLuaScript("return nil;"));
        String scriptHash = redisClient.loadLuaScriptFile("script/test.lua");
        Assertions.assertNotNull(scriptHash);
        Assertions.assertTrue(redisClient.scriptExists(scriptHash));
        String content = UUID.randomUUID().toString();
        redisClient.set("test", content.getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(content, new String(redisClient.executeScript(scriptHash, 1, "test".getBytes(StandardCharsets.UTF_8))));
        redisClient.hset("testHash", "test", "test".getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals("test", new String(redisClient.hget("testHash", "test")));
        redisClient.hdelete("testHash", "test");
        Assertions.assertNull(redisClient.hget("testHash", "test"));
        redisClient.hsetnx("testHash", "test", "test".getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals("test", new String(redisClient.hget("testHash", "test")));
        Assertions.assertEquals(Duration.ofMillis(Long.MAX_VALUE), redisClient.ttl("test"));
        Assertions.assertNull(redisClient.ttl("asdakjlsdhakljshjdkahskjhd"));
        long numberContent = 100;
        redisClient.set("test", new LongSerializer().serialize(numberContent));
        Assertions.assertEquals(numberContent, new LongSerializer().deserialize(redisClient.executeScript(scriptHash, 1, "test".getBytes(StandardCharsets.UTF_8))));
    }

    @Test
    @DisplayName("测试incr的脚本")
    public void testIncrScript() throws IOException {
        TestRedisClient redisClient = new TestRedisClient(connectionFactory);
        Assertions.assertNotNull(redisClient.loadLuaScript("return nil;"));
        String scriptHash = redisClient.loadLuaScriptFile("script/incr.lua");
        Assertions.assertNotNull(scriptHash);
        Assertions.assertTrue(redisClient.executeScript(scriptHash, Long.class, 1, "test-incr".getBytes(StandardCharsets.UTF_8)) > 0L);
    }
}
