package io.gardenerframework.fragrans.cache.test.caces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.fragrans.cache.test.SpringRedisCacheTestApplication;
import io.gardenerframework.fragrans.data.cache.client.RedisCacheClient;
import io.gardenerframework.fragrans.data.cache.client.SpringRedisCacheClient;
import io.gardenerframework.fragrans.data.cache.serialize.JdkSerializer;
import io.gardenerframework.fragrans.data.cache.serialize.JsonSerializer;
import io.gardenerframework.fragrans.data.cache.serialize.StringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/2/17 7:33 下午
 */
@SpringBootTest(classes = SpringRedisCacheTestApplication.class)
public class SpringRedisCacheClientTest {
    private final StringSerializer stringSerializer = new StringSerializer();
    private final JsonSerializer<TestObject> jsonSerializer = new JsonSerializer(new ObjectMapper(), TestObject.class);
    private final JdkSerializer<JdkTestObject> jdkSerializer = new JdkSerializer<>();
    @Autowired
    private RedisCacheClient cacheClient;
    @Autowired
    private RedisConnectionFactory connectionFactory;
    private RedisTemplate<String, ?> redisTemplate;

    @Test
    @DisplayName("进行 redis 客户端的冒烟测试")
    public void springRedisClientSmokeTest() throws JsonProcessingException {
        Assertions.assertTrue(cacheClient instanceof SpringRedisCacheClient);
        String key = UUID.randomUUID().toString();
        String value = UUID.randomUUID().toString();
        cacheClient.set(key, stringSerializer.serialize(value));
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        String stored = new String(Objects.requireNonNull(redisTemplate.execute(new RedisCallback<byte[]>() {
            @Nullable
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(new StringSerializer().serialize(key));
            }
        })), StandardCharsets.UTF_8);
        Assertions.assertEquals(value, stored);
        Assertions.assertEquals(value, stringSerializer.deserialize(cacheClient.get(key)));
        TestObject testObject = new TestObject(UUID.randomUUID().toString());
        cacheClient.set(key, jsonSerializer.serialize(testObject));
        stored = new String(Objects.requireNonNull(redisTemplate.execute(new RedisCallback<byte[]>() {
            @Nullable
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(new StringSerializer().serialize(key));
            }
        })), StandardCharsets.UTF_8);
        Assertions.assertEquals(new ObjectMapper().writeValueAsString(testObject), stored);
        Assertions.assertEquals(testObject, jsonSerializer.deserialize(cacheClient.get(key)));

        JdkTestObject jdkTestObject = new JdkTestObject(UUID.randomUUID().toString(), Collections.singletonList(new JdkTestObject(UUID.randomUUID().toString(), null)));

        cacheClient.set(key, jdkSerializer.serialize(jdkTestObject));
        Assertions.assertEquals(jdkTestObject, jdkSerializer.deserialize(cacheClient.get(key)));

        Assertions.assertTrue(cacheClient.setIfPresents(key, jdkSerializer.serialize(jdkTestObject)));
        Assertions.assertFalse(cacheClient.setIfNotPresents(key, jdkSerializer.serialize(jdkTestObject)));

        cacheClient.delete(key);
        Assertions.assertFalse(cacheClient.setIfPresents(key, jdkSerializer.serialize(jdkTestObject)));
        Assertions.assertTrue(cacheClient.setIfNotPresents(key, jdkSerializer.serialize(jdkTestObject)));
    }

    @DisplayName("测试脚本运行")
    @Test
    public void testRedisScript() throws IOException {
        RedisCacheClient redisClient = new SpringRedisCacheClient(connectionFactory);
        Assertions.assertNotNull(redisClient.loadLuaScript("return nil;"));
        String scriptHash = redisClient.loadLuaScriptFile("script/test.lua");
        Assertions.assertNotNull(scriptHash);
        String content = UUID.randomUUID().toString();
        redisClient.set("test", content.getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(content, new String(redisClient.executeScript(scriptHash, 1, "test".getBytes(StandardCharsets.UTF_8))));
    }

    public interface TestInterface extends Serializable {

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestObject {
        private String field;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JdkTestObject implements TestInterface, Serializable {
        private String field;
        private List<TestInterface> collection;
    }
}
