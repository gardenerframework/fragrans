package com.jdcloud.gardener.fragrans.data.persistence.orm.mapping.annotation;

import java.lang.reflect.Method;

/**
 * @author zhanghan30
 * @date 2022/9/25 05:00
 */
public interface ColumnTypeHandlerProvider {
    /**
     * 给出列的映射关系
     *
     * @param mapperInterface 当前正在调用的mapper类型
     * @param mapperMethod    当前正在使用的mapper方法
     * @return 关系
     */
    ColumnMapping provide(Class<?> mapperInterface, Method mapperMethod);
}
