package com.jdcloud.gardener.fragrans.event.schema;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2021/10/26 4:45 下午
 */
@Data
@SuperBuilder
public class UpdateMessageTemplate<O> {
    private O from;
    private O to;
}
