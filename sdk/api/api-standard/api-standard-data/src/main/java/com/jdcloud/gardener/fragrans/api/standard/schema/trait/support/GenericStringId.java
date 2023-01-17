package com.jdcloud.gardener.fragrans.api.standard.schema.trait.support;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.validation.constraints.text.OptionalNonBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2022/6/23 12:46 下午
 */
@Getter
@Setter
public class GenericStringId implements ApiStandardDataTraits.Id<String> {
    @OptionalNonBlank
    private String id;
}
