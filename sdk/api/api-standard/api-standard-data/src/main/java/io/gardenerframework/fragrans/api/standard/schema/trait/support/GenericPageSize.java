package io.gardenerframework.fragrans.api.standard.schema.trait.support;

import io.gardenerframework.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import io.gardenerframework.fragrans.validation.constraints.range.Max;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;

/**
 * @author zhanghan30
 * @date 2022/6/23 12:54 下午
 */
@Getter
@Setter
public class GenericPageSize implements ApiStandardDataTraits.PageSize {
    @Positive
    @Max(provider = GenericMaxPageSizeProvider.class)
    private Integer pageSize;
}
