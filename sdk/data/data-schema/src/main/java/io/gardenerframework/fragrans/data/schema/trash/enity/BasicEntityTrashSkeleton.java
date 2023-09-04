package io.gardenerframework.fragrans.data.schema.trash.enity;

import io.gardenerframework.fragrans.data.schema.trash.BasicTrashSkeleton;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;

/**
 * @author zhanghan30
 * @date 2023/9/4 16:11
 */
public interface BasicEntityTrashSkeleton<T, I> extends
        BasicTrashSkeleton<I>,
        GenericTraits.IdentifierTraits.Id<T> {
}
