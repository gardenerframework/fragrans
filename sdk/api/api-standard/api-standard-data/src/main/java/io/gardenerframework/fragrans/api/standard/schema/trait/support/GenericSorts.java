package io.gardenerframework.fragrans.api.standard.schema.trait.support;

import io.gardenerframework.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2022/6/23 1:00 下午
 */
@Setter
public class GenericSorts implements ApiStandardDataTraits.Sorts {
    private Collection<ApiStandardDataTraits.@NotNull @Valid Sort> sorts;

    /**
     * 如果排序列清单为null，则返回空的列表
     *
     * @return 排序列清单
     */
    @Override
    public Collection<ApiStandardDataTraits.Sort> getSorts() {
        return sorts == null ? Collections.emptyList() : sorts;
    }
}
