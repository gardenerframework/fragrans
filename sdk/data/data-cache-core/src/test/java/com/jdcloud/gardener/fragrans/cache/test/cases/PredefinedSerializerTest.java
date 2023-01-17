package com.jdcloud.gardener.fragrans.cache.test.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcloud.gardener.fragrans.cache.test.CacheCoreTestApplication;
import com.jdcloud.gardener.fragrans.data.cache.serialize.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author zhanghan30
 * @date 2022/2/17 3:52 下午
 */
@SpringBootTest(classes = CacheCoreTestApplication.class)
@DisplayName("序列化与反序列化测试")
public class PredefinedSerializerTest {
    @DisplayName("json序列化测试")
    @Test
    public void JsonSerializerSmokeTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        TestObject testObject = new TestObject(UUID.randomUUID().toString(), Collections.singletonList(new Random().nextInt()));
        JsonSerializer<TestObject> jsonSerializer = new JsonSerializer<>(objectMapper, TestObject.class);
        byte[] stream = jsonSerializer.serialize(testObject);
        Assertions.assertArrayEquals(objectMapper.writeValueAsBytes(testObject), stream);
        Assertions.assertEquals(testObject, jsonSerializer.deserialize(stream));
    }

    @DisplayName("jdk序列化测试")
    @Test
    public void JdkSerializerSmokeTest() {
        String testString = UUID.randomUUID().toString();
        JdkSerializer<String> serializer = new JdkSerializer<>();
        byte[] stream = serializer.serialize(testString);
        Assertions.assertNotNull(stream);
        Assertions.assertEquals(testString, serializer.deserialize(stream));
        JdkTestObject testObject = new JdkTestObject(UUID.randomUUID().toString(), Collections.singletonList(new Random().nextInt()));
        JdkSerializer<JdkTestObject> jdkTestObjectJdkSerializer = new JdkSerializer<>();
        stream = jdkTestObjectJdkSerializer.serialize(testObject);
        Assertions.assertNotNull(stream);
        Assertions.assertEquals(testObject, jdkTestObjectJdkSerializer.deserialize(stream));
    }


    @Test
    @DisplayName("数字序列化测试")
    public void numberSerializerSmokeTest() {
        Map<Number, NumberSerializer> testPair = new HashMap<>();
        testPair.put(Byte.valueOf("124"), new ByteSerializer());
        testPair.put(Short.valueOf("10000"), new ShortSerializer());
        testPair.put(new Random().nextInt(), new IntegerSerializer());
        testPair.put(Integer.MAX_VALUE + 20L, new LongSerializer());
        testPair.put(new Random().nextFloat(), new FloatSerializer());
        testPair.put(new Random().nextDouble(), new DoubleSerializer());
        testPair.forEach(
                (value, serializer) -> {
                    byte[] stream = serializer.serialize(value);
                    Assertions.assertArrayEquals(String.valueOf(value).getBytes(StandardCharsets.UTF_8), stream);
                    Assertions.assertEquals(value, serializer.deserialize(stream));
                }
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestObject {
        private String field;
        private List<Integer> collection;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JdkTestObject implements Serializable {
        private String field;
        private List<Integer> collection;
    }
}
