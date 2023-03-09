package io.gardenerframework.fragrans.api.options.test.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.fragrans.api.options.exception.client.InvalidApiOptionException;
import io.gardenerframework.fragrans.api.options.schema.response.ReadApiOptionRegistryItemResponse;
import io.gardenerframework.fragrans.api.options.schema.response.ReadApiOptionRegistryResponse;
import io.gardenerframework.fragrans.api.options.test.ApiOptionsEngineTestApplication;
import io.gardenerframework.fragrans.api.options.test.SimplePlainOption;
import io.gardenerframework.fragrans.api.standard.schema.ApiError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author zhanghan30
 * @date 2022/1/3 6:42 下午
 */
@SpringBootTest(classes = ApiOptionsEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("api选项接口测试")
public class ApiOptionsEndpointTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("动态地址测试")
    public void dynamicPathTest() {
        new RestTemplate().getForObject("http://localhost:{port}" + "/options", ReadApiOptionRegistryResponse.class, port);
    }

    @Test
    @DisplayName("接口冒烟测试")
    public void smokeTest() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        //读取注册表
        ReadApiOptionRegistryResponse registry = restTemplate.getForObject("http://localhost:{port}" + "/options", ReadApiOptionRegistryResponse.class, port);
        String id = "simplePlainOption";
        //读取单个选项
        ReadApiOptionRegistryItemResponse item = restTemplate.getForObject("http://localhost:{port}" + "/options/{id}", ReadApiOptionRegistryItemResponse.class, port, id);
        Map<String, Object> option = (Map<String, Object>) item.getOption();
        //设置一个不合法值
        option.put("stringField", "   ");
        boolean exceptionCaught = false;
        try {
            restTemplate.put("http://localhost:{port}" + "/options/{id}", option, port, id);
        } catch (HttpClientErrorException exception) {
            ApiError apiError = mapper.readValue(exception.getResponseBodyAsString(), ApiError.class);
            Assertions.assertEquals(InvalidApiOptionException.class.getCanonicalName(), apiError.getError());
            exceptionCaught = true;
        }
        Assertions.assertTrue(exceptionCaught);
        //设置为合法值
        option.put("stringField", UUID.randomUUID().toString());
        option.put("readOnly", UUID.randomUUID().toString());
        restTemplate.put("http://localhost:{port}" + "/options/{id}", option, port, id);
        item = restTemplate.getForObject("http://localhost:{port}" + "/options/{id}", ReadApiOptionRegistryItemResponse.class, port, id);
        Assertions.assertEquals(option.get("stringField"), ((Map<?, ?>) item.getOption()).get("stringField"));
        Assertions.assertNull(((Map<?, ?>) item.getOption()).get("readOnly"));
    }

    @Test
    @DisplayName("内嵌的选项测试")
    public void nestedOptionTest() {
        String id = "mapAndListNestedOption";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, List<String>> nested = new HashMap<>();
        Map<String, Object> option = new HashMap<>();
        option.put("nested", nested);
        nested.put(UUID.randomUUID().toString(), Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        restTemplate.put("http://localhost:{port}" + "/options/{id}", option, port, id);
        ReadApiOptionRegistryItemResponse item = restTemplate.getForObject("http://localhost:{port}" + "/options/{id}", ReadApiOptionRegistryItemResponse.class, port, id);
        Assertions.assertEquals(nested, ((Map<String, Object>) item.getOption()).get("nested"));
        List<SimplePlainOption> simples = Arrays.asList(
                new SimplePlainOption(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                new SimplePlainOption(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        );
        option.put("simples", simples);
        restTemplate.put("http://localhost:{port}" + "/options/{id}", option, port, id);
        item = restTemplate.getForObject("http://localhost:{port}" + "/options/{id}", ReadApiOptionRegistryItemResponse.class, port, id);
        Assertions.assertEquals(mapper.convertValue(simples.stream().map(simple -> {
            simple.setReadOnly(null);
            return simple;
        }), new TypeReference<List<Map<String, Object>>>() {
        }), ((Map<String, Object>) item.getOption()).get("simples"));
    }
}
