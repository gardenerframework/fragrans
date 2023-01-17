package com.jdcloud.gardener.fragrans.log;

import com.jdcloud.gardener.fragrans.event.schema.DomainEvent;
import com.jdcloud.gardener.fragrans.log.schema.operation.action.Send;
import com.jdcloud.gardener.fragrans.log.schema.operation.state.Done;
import com.jdcloud.gardener.fragrans.log.schema.payload.DomainEventPayload;
import org.slf4j.Logger;

/**
 * @author zhanghan30
 * @date 2021/11/12 9:32 下午
 */
public abstract class DomainEventLogWriter {
    private DomainEventLogWriter() {

    }

    /**
     * 写领域事件发送成功日子
     *
     * @param logger logger
     * @param topic  主题
     * @param id     id
     * @param type   类型
     */
    public static void writeSendEventInfoLog(
            Logger logger,
            String topic,
            String id,
            String type
    ) {
        OperationLogWriter.writeGenericRawInfoLog(
                logger,
                new Done(),
                new Send(),
                DomainEvent.class,
                null,
                new DomainEventPayload(id, topic, type),
                null
        );
    }
}
