package com.jdcloud.gardener.fragrans.api.standard.schema.trait.support;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.validation.constraints.text.OptionalNonBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2022/6/23 12:50 下午
 */
@Getter
@Setter
public class GenericPageMarker implements ApiStandardDataTraits.PageMarker {
    @OptionalNonBlank
    private String marker;
}
