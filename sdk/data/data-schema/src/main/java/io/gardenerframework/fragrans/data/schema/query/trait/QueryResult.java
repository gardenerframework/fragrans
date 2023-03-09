package io.gardenerframework.fragrans.data.schema.query.trait;

import io.gardenerframework.fragrans.data.trait.structure.DataStructureTraits;

/**
 * 标记性接口，代表这是一个查询结果
 *
 * @author zhanghan30
 * @date 2022/8/26 10:57 下午
 */
public interface QueryResult<C> extends
        DataStructureTraits.CollectionTraits.Contents<C>,
        DataStructureTraits.CollectionTraits.TotalNumber {
}
