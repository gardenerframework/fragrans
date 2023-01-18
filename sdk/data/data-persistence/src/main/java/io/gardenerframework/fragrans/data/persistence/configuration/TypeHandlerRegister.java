package io.gardenerframework.fragrans.data.persistence.configuration;

import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.function.Consumer;

/**
 * @author zhanghan30
 * @date 2021/10/22 6:33 下午
 */
@FunctionalInterface
public interface TypeHandlerRegister extends Consumer<TypeHandlerRegistry> {
}
