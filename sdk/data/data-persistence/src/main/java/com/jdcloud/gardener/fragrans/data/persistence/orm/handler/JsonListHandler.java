package com.jdcloud.gardener.fragrans.data.persistence.orm.handler;

import java.lang.reflect.Type;
import java.util.AbstractCollection;
import java.util.List;

/**
 * 用于处理通用的列表
 * <p>
 * 这里注意有坑
 * <p>
 * mybatis在遍历类型时不遍历接口{@link org.apache.ibatis.type.TypeHandlerRegistry#getJdbcHandlerMapForSuperclass(Class)}
 * <p>
 * 所以只能注册为类的类型而不是接口
 * <p>
 * 否则传入的参数一辈子也找不到处理器(因为传入的参数的getClass是具体的实现类而不是个接口)
 *
 * @author zhanghan30
 * @date 2022/9/25 02:57
 */
@SuppressWarnings("rawtypes")
public class JsonListHandler extends JsonTypeHandler<AbstractCollection> {
    @Override
    public Type getTypeReference() {
        return List.class;
    }
}
