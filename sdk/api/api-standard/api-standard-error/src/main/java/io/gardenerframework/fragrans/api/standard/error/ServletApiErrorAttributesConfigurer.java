package io.gardenerframework.fragrans.api.standard.error;

import java.util.function.Consumer;

/**
 * @author zhanghan30
 * @date 2022/7/6 5:48 下午
 */
@FunctionalInterface
public interface ServletApiErrorAttributesConfigurer extends Consumer<ServletApiErrorAttributes> {
}
