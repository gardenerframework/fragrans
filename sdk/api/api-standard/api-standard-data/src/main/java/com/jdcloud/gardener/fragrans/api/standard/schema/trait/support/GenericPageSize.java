package com.jdcloud.gardener.fragrans.api.standard.schema.trait.support;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.validation.constraints.range.Max;
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
