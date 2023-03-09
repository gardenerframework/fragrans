package io.gardenerframework.fragrans.api.standard.schema.trait.support;

import io.gardenerframework.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import io.gardenerframework.fragrans.validation.constraints.text.OptionalNonBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2022/6/23 12:49 下午
 */
@Getter
@Setter
public class GenericKeyword implements ApiStandardDataTraits.Keyword {
    @OptionalNonBlank
    private String keyword;
}
