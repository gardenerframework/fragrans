package com.jdcloud.gardener.fragrans.event.type;

import com.google.common.base.CaseFormat;

/**
 * 事件类型的type safe
 *
 * @author zhanghan30
 * @date 2021/10/27 7:39 下午
 */
public interface EventType {
    /**
     * 获得事件类型
     *
     * @return 类型
     */
    default String getType() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, this.getClass().getSimpleName());
    }
}
