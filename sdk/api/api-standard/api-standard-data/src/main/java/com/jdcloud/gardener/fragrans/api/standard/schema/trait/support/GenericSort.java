package com.jdcloud.gardener.fragrans.api.standard.schema.trait.support;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghan30
 * @date 2022/6/23 12:58 下午
 */
@Getter
@Setter
public class GenericSort implements ApiStandardDataTraits.Sort {
    private String sort;
    private Order order;

    /**
     * 默认使用升序
     *
     * @return 顺序
     */
    @Override
    public Order getOrder() {
        return order == null ? Order.ASC : order;
    }
}
