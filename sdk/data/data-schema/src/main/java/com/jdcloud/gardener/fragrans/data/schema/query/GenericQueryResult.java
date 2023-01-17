package com.jdcloud.gardener.fragrans.data.schema.query;

import com.jdcloud.gardener.fragrans.data.schema.query.trait.QueryResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/8/26 10:59 下午
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GenericQueryResult<C> implements QueryResult<C> {
    /**
     * 内容物
     */
    private Collection<C> contents;
    /**
     * 总数，如果查询的时候没有要求给总数，则可能没有
     */
    private Long total;
}
