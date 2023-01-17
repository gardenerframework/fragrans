package com.jdcloud.gardener.fragrans.data.persistence.orm.mapping.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author zhanghan30
 * @date 2022/9/25 05:01
 */
@AllArgsConstructor
@Getter
public class ColumnMapping {
    /**
     * 记录集中的哪个列
     */
    private final String column;
    /**
     * 对应返回数据的哪个字段
     */
    private final String field;
    /**
     * 类型处理器
     */
    private final TypeHandler<?> handler;
    /**
     * 类型
     */
    private final Class<?> javaType;
}
