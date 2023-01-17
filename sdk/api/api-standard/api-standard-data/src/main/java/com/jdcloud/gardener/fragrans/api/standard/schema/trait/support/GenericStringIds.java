package com.jdcloud.gardener.fragrans.api.standard.schema.trait.support;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2022/6/23 12:47 下午
 */
@Setter
public class GenericStringIds implements ApiStandardDataTraits.Ids<String> {
    private Collection<@NotBlank String> ids;

    @Override
    public Collection<String> getIds() {
        return ids == null ? Collections.emptyList() : ids;
    }
}
