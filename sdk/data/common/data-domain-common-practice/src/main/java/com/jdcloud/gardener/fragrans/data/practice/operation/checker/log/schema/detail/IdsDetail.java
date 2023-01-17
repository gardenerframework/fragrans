package com.jdcloud.gardener.fragrans.data.practice.operation.checker.log.schema.detail;

import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/6/17 2:58 下午
 */
@AllArgsConstructor
@Getter
public class IdsDetail<I> implements Detail {
    private final Collection<I> ids;
}
