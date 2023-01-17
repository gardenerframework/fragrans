package com.jdcloud.gardener.fragrans.api.options.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/1/3 4:11 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiOptionRegistryItem {
    /**
     * 具体的选项内容
     */
    private Object option;
    /**
     * 选项的名字
     */
    private String name;
    /**
     * 是否只读
     */
    private boolean readonly;
    /**
     * 版本号
     */
    @Nullable
    private String versionNumber;
}
