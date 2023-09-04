package io.gardenerframework.fragrans.data.schema.trash;

import io.gardenerframework.fragrans.data.schema.common.BasicRecordSkeleton;
import io.gardenerframework.fragrans.data.schema.trash.trait.Item;

/**
 * @author zhanghan30
 * @date 2023/9/4 16:09
 */
public interface BasicTrashSkeleton<I> extends
        BasicRecordSkeleton,
        Item<I> {
}
