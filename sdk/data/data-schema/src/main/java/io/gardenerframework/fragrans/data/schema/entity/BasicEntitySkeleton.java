package io.gardenerframework.fragrans.data.schema.entity;

import io.gardenerframework.fragrans.data.schema.common.BasicRecordSkeleton;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;

/**
 * @author zhanghan30
 * @date 2023/9/4 16:01
 */
public interface BasicEntitySkeleton<T> extends
        BasicRecordSkeleton,
        GenericTraits.IdentifierTraits.Id<T> {
}
