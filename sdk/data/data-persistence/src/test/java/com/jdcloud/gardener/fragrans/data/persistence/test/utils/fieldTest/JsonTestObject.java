package com.jdcloud.gardener.fragrans.data.persistence.test.utils.fieldTest;

import com.jdcloud.gardener.fragrans.data.persistence.configuration.TypeHandlerRegister;
import com.jdcloud.gardener.fragrans.data.persistence.orm.handler.JsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/9/25 01:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonTestObject {
    private Collection<String> primitive;
    private Collection<Nested> list;
    private Map<String, Object> map;
    private Nested object;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Nested {
        private String test;
    }

    @Component
    @NoArgsConstructor
    public static class NestTypeHandler extends JsonTypeHandler<Nested> implements TypeHandlerRegister {

        @Override
        public void accept(TypeHandlerRegistry typeHandlerRegistry) {
            typeHandlerRegistry.register(NestTypeHandler.class);
        }
    }
}
