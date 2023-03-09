package com.jdcloud.gardener.fragrans.log.schema.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2021/11/12 9:34 下午
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DomainEventPayload implements Payload {
    private String id;
    private String topic;
    private String type;
}
