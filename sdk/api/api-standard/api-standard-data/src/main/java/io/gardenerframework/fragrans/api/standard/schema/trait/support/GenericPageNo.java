package io.gardenerframework.fragrans.api.standard.schema.trait.support;

import io.gardenerframework.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import lombok.Setter;

import javax.validation.constraints.Positive;

/**
 * @author zhanghan30
 * @date 2022/6/23 12:53 下午
 */
@Setter
public class GenericPageNo implements ApiStandardDataTraits.PageNo {
    @Positive
    private Integer pageNo;


    @Override
    public Integer getPageNo() {
        return pageNo == null ? 1 : pageNo;
    }
}
