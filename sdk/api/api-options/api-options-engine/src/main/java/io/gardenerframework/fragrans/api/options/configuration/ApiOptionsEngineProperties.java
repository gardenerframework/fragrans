package io.gardenerframework.fragrans.api.options.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/7/19 4:04 下午
 */
@ApiOptionsEngineComponent
@Getter
@Setter
public class ApiOptionsEngineProperties {
    private final String instanceId = UUID.randomUUID().toString();
}
