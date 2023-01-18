package io.gardenerframework.fragrans.data.persistence.test.utils.template;

import org.apache.ibatis.annotations.SelectProvider;

/**
 * @author zhanghan30
 * @date 2022/9/23 02:56
 */
public interface MapperTemplate<A> {
    @SelectProvider(MapperSqlTemplate.class)
    A select(String any);
}
