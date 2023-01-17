package com.jdcloud.gardener.fragrans.api.options.schema.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/1/3 7:08 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadApiOptionRegistryResponse {
    private Map<String, ReadApiOptionRegistryItemResponse> options;
}
