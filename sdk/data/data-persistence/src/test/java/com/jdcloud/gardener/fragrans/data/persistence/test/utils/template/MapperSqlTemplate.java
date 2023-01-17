package com.jdcloud.gardener.fragrans.data.persistence.test.utils.template;

import lombok.NoArgsConstructor;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author zhanghan30
 * @date 2022/9/23 02:56
 */
@NoArgsConstructor
public class MapperSqlTemplate<A> implements ProviderMethodResolver {
    public String select(String any) {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type actualTypeArgument = type.getActualTypeArguments()[0];
        if (String.class.equals(actualTypeArgument)) {
            return String.format("select \"%s\" from dual", actualTypeArgument.getTypeName());
        } else if (Integer.class.equals(actualTypeArgument)) {
            return "select 1 from dual";
        }
        return null;
    }
}
