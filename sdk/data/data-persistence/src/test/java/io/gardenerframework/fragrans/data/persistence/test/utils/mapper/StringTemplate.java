package io.gardenerframework.fragrans.data.persistence.test.utils.mapper;

import io.gardenerframework.fragrans.data.persistence.annotation.OverrideSqlProviderAnnotation;
import io.gardenerframework.fragrans.data.persistence.test.utils.template.MapperTemplate;

/**
 * @author zhanghan30
 * @date 2022/9/23 02:58
 */
@OverrideSqlProviderAnnotation(StringSqlTemplate.class)
public interface StringTemplate extends MapperTemplate<String> {

}
