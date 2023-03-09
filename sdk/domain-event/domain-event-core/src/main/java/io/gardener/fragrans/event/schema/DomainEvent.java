package com.jdcloud.gardener.fragrans.event.schema;

import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zhanghan30
 * @date 2021/10/26 3:26 下午
 */
@Data
@AllArgsConstructor
@LogTarget("领域事件")
public class DomainEvent {
    /**
     * 事件序号
     */
    private String id;
    /**
     * 事件类型
     */
    private String type;
    /**
     * 事件数据
     */
    private Object payload;
}
