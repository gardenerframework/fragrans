package com.jdcloud.gardener.fragrans.api.standard.schema.trait.support;

import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2022/6/23 12:45 下午
 */
@Setter
public class GenericContents<C> implements ApiStandardDataTraits.Contents<C> {
    /**
     * 数据内容
     */
    @Valid
    private Collection<@NotNull C> contents;

    /**
     * 如果内容为null，则返回空的列表
     *
     * @return 内容
     */
    @Override
    public Collection<C> getContents() {
        return contents == null ? Collections.emptyList() : contents;
    }
}
