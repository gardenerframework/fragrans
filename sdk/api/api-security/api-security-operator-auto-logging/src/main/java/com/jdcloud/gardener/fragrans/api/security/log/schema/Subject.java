package com.jdcloud.gardener.fragrans.api.security.log.schema;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhanghan30
 * @date 2022/6/14 2:39 下午
 */
@Getter
@AllArgsConstructor
public class Subject {
    private final String id;

    @Override
    public String toString() {
        return id;
    }
}
