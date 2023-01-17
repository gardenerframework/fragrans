package com.jdcloud.gardener.fragrans.api.options.endpoint;

import com.jdcloud.gardener.fragrans.api.options.schema.response.ReadApiOptionRegistryItemResponse;
import com.jdcloud.gardener.fragrans.api.options.schema.response.ReadApiOptionRegistryResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/1/3 7:07 下午
 */
public interface ApiOptionsEndpointSkeleton {
    /**
     * 获取所有api选项
     *
     * @return 选项
     */
    ReadApiOptionRegistryResponse readApiOptionRegistry();

    /**
     * 读取指定的选项
     *
     * @param id id
     * @return 选项
     */
    ReadApiOptionRegistryItemResponse readApiOptionRegistryItem(@Valid @NotBlank String id);

    /**
     * 保存api选项
     *
     * @param id        id
     * @param newOption 选项值
     */
    void saveApiOption(@Valid @NotBlank String id, @Valid Map<String, Object> newOption);
}
