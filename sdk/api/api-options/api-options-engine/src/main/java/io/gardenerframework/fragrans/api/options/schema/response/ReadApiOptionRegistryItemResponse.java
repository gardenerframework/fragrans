package io.gardenerframework.fragrans.api.options.schema.response;

import io.gardenerframework.fragrans.api.options.schema.ApiOptionRegistryItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/5/10 3:59 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ReadApiOptionRegistryItemResponse extends ApiOptionRegistryItem {
    private String description;

    public ReadApiOptionRegistryItemResponse(Object option, String name, boolean readonly, @Nullable String versionNumber, String description) {
        super(option, name, readonly, versionNumber);
        this.description = description;
    }
}
