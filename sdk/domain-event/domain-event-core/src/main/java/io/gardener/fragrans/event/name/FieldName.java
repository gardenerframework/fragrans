package com.jdcloud.gardener.fragrans.event.name;

import com.google.common.base.CaseFormat;

/**
 * @author zhanghan30
 * @date 2021/10/27 9:03 下午
 */
public interface FieldName {
    default String getName() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, this.getClass().getSimpleName());
    }
}
