package com.jdcloud.gardener.fragrans.api.group.policy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhanghan30
 * @date 2022/6/24 5:55 下午
 */
@AllArgsConstructor
@Getter
public class ApiGroupContextPathPolicy implements ApiGroupPolicy {
    private final String contextPath;
}
