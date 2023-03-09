package io.gardenerframework.fragrans.data.persistence.configuration;

import io.gardenerframework.fragrans.data.persistence.orm.handler.JsonListHandler;
import io.gardenerframework.fragrans.data.persistence.orm.handler.JsonMapHandler;
import io.gardenerframework.fragrans.data.persistence.orm.handler.JsonSetHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhanghan30
 * @date 2022/1/13 1:29 上午
 */
@Configuration
public class DataPersistenceMybatisConfiguration {
    private final List<TypeHandlerRegister> registers;

    DataPersistenceMybatisConfiguration(List<TypeHandlerRegister> registers) {
        this.registers = registers;
    }

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            configuration.setMapUnderscoreToCamelCase(true);
            //将任何list类型的属性转为json数组
            //注册为写入(自动从范型读取)
            configuration.getTypeHandlerRegistry().register(JsonListHandler.class);
            configuration.getTypeHandlerRegistry().register(JsonSetHandler.class);
            configuration.getTypeHandlerRegistry().register(JsonMapHandler.class);
            //读取后反序列化 - 只能用于原始类型
            configuration.getTypeHandlerRegistry().register(Collection.class, JsonListHandler.class);
            configuration.getTypeHandlerRegistry().register(List.class, JsonListHandler.class);
            configuration.getTypeHandlerRegistry().register(Set.class, JsonSetHandler.class);
            configuration.getTypeHandlerRegistry().register(Map.class, JsonMapHandler.class);
            //其余注册器自行完成类型注册过程
            this.registers.forEach(
                    register -> register.accept(configuration.getTypeHandlerRegistry())
            );
        };
    }
}
