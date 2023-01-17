package com.jdcloud.gardener.fragrans.api.standard.schema.test;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.support.GenericKeyword;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.support.GenericPageNo;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;

import javax.validation.Valid;

/**
 * @author zhanghan30
 * @date 2022/8/23 5:51 下午
 */
@Builder
@NoArgsConstructor
public class TestDelegateObject implements
        ApiStandardDataTraits.Keyword,
        ApiStandardDataTraits.PageNo {
    @Valid
    @Delegate
    private final ApiStandardDataTraits.Keyword keyword = new GenericKeyword();
    @Valid
    @Delegate
    private final ApiStandardDataTraits.PageNo pageNo = new GenericPageNo();
}
