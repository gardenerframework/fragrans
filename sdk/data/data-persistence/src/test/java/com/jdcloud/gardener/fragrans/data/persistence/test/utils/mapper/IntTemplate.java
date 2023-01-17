package com.jdcloud.gardener.fragrans.data.persistence.test.utils.mapper;

import com.jdcloud.gardener.fragrans.data.persistence.annotation.OverrideSqlProviderAnnotation;
import com.jdcloud.gardener.fragrans.data.persistence.test.utils.template.MapperTemplate;

/**
 * @author zhanghan30
 * @date 2022/9/23 02:58
 */
@OverrideSqlProviderAnnotation(intSqlTemplate.class)
public interface IntTemplate extends MapperTemplate<Integer> {
}
