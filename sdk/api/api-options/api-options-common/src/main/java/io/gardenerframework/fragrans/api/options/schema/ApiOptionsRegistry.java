package io.gardenerframework.fragrans.api.options.schema;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 选项注册表
 *
 * @author zhanghan30
 * @date 2022/1/3 4:10 下午
 */
public class ApiOptionsRegistry {
    private final Map<String, ApiOptionRegistryItem> registry = new ConcurrentHashMap<>();

    /**
     * 获取表项
     *
     * @param id id
     * @return 项目
     */
    @Nullable
    public ApiOptionRegistryItem getItem(String id) {
        return registry.get(id);
    }

    /**
     * 设置项，如果存在就覆盖
     *
     * @param id   id
     * @param item 项目
     */
    public void setItem(String id, ApiOptionRegistryItem item) {
        registry.put(id, item);
    }

    /**
     * 获取已经注册的id
     *
     * @return id清单
     */
    public Collection<String> getIds() {
        return registry.keySet();
    }
}
