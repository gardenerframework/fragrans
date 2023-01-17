package com.jdcloud.gardener.fragrans.api.standard.schema.trait.support;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.PositiveOrZero;

/**
 * @author zhanghan30
 * @date 2022/6/23 12:58 下午
 */
@Getter
@Setter
public class GenericTotalNumber implements ApiStandardDataTraits.TotalNumber {
    @PositiveOrZero
    private Long total;
}
