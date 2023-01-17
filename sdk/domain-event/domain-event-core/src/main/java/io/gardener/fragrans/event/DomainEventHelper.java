package com.jdcloud.gardener.fragrans.event;

import com.jdcloud.gardener.fragrans.event.name.FieldName;
import com.jdcloud.gardener.fragrans.event.schema.DomainEvent;
import com.jdcloud.gardener.fragrans.event.schema.EntityFieldChangedMessageTemplate;
import com.jdcloud.gardener.fragrans.event.schema.RelationFieldChangedMessageTemplate;
import com.jdcloud.gardener.fragrans.event.schema.UpdateMessageTemplate;
import com.jdcloud.gardener.fragrans.event.type.EventType;
import com.jdcloud.gardener.fragrans.event.type.standard.CreateRecord;
import com.jdcloud.gardener.fragrans.event.type.standard.DeleteRecord;
import com.jdcloud.gardener.fragrans.event.type.standard.FieldChanged;
import com.jdcloud.gardener.fragrans.event.type.standard.UpdateRecord;
import com.jdcloud.gardener.fragrans.log.DomainEventLogWriter;
import com.jdcloud.gardener.fragrans.schema.entity.BasicRecord;
import com.jdcloud.gardener.fragrans.data.unique.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Date;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2021/10/26 3:08 下午
 */
@Slf4j
public abstract class DomainEventHelper {
    private static DomainEventSender eventSender = null;
    private static UniqueIdGenerator uniqueIdGenerator;

    private static boolean initialized = false;

    private DomainEventHelper() {

    }

    public static synchronized void init(
            DomainEventSender eventSender,
            UniqueIdGenerator uniqueIdGenerator
    ) {
        if (!initialized) {
            DomainEventHelper.eventSender = eventSender;
            DomainEventHelper.uniqueIdGenerator = uniqueIdGenerator;
            initialized = true;
        }
    }

    /**
     * 扫描主题
     *
     * @param recordClass 待扫描类型
     * @param <C>         类型
     * @return 主题
     */
    private static <C> String scanTopic(Class<C> recordClass) {
        Class<?> userClass = ClassUtils.getUserClass(recordClass);
        Topic topic = AnnotationUtils.findAnnotation(userClass, Topic.class);
        if (topic == null) {
            throw new IllegalArgumentException("no @Topic annotation found on Class " + userClass.getCanonicalName());
        }
        return topic.value();
    }

    /**
     * 扫描主题
     *
     * @param object 待扫描对象
     * @param <R>    类型
     * @return 主题
     */
    private static <R> String scanTopic(R object) {
        return scanTopic(object.getClass());
    }

    /**
     * 发送领域事件
     *
     * @param type    类型
     * @param payload 消息
     */
    public static <R> void sendEvent(EventType type, R payload) {
        sendEvent(scanTopic(payload), type, payload);
    }

    /**
     * 发送领域事件
     *
     * @param topic   频道
     * @param type    类型
     * @param payload 消息数据
     */
    public static <R> void sendEvent(String topic, EventType type, R payload) {
        sendRawEvent(topic, type.getType(), payload);
    }

    /**
     * 发送原始事件
     *
     * @param topic   主题
     * @param type    类型
     * @param payload 载荷
     * @param <P>     记录类型
     */
    public static <P> void sendRawEvent(String topic, String type, P payload) {
        Assert.notNull(topic, "topic不能为null");
        Assert.notNull(type, "事件类型不能为null");
        Assert.notNull(payload, "消息载荷不能为null");
        DomainEvent event = new DomainEvent(uniqueIdGenerator.nextId('E'), type, payload);
        eventSender.sendEvent(topic, event);
        DomainEventLogWriter.writeSendEventInfoLog(log, topic, event.getId(), type);
    }

    /**
     * 发送记录创建事件
     *
     * @param record 创建的记录
     */
    public static <R> void sendCreateRecordEvent(R record) {
        if (record instanceof BasicRecord && ((BasicRecord) record).getCreatedTime() == null) {
            ((BasicRecord) record).setCreatedTime(new Date());
        }
        sendEvent(new CreateRecord(), record);
    }

    /**
     * 发送记录变更事件
     *
     * @param from 从什么
     * @param to   更新成什么
     * @param <R>  类型
     */
    public static <R> void sendUpdateRecordEvent(R from, R to) {
        if (to instanceof BasicRecord) {
            ((BasicRecord) to).setLastUpdateTime(new Date());
        }
        sendEvent(scanTopic(from), new UpdateRecord(), UpdateMessageTemplate.<R>builder().from(from).to(to).build());
    }

    /**
     * 发送删除记录事件
     *
     * @param record 记录
     * @param <R>    类型
     */
    public static <R> void sendDeleteRecordEvent(R record) {
        sendEvent(new DeleteRecord(), record);
    }

    /**
     * 属性变更事件
     * <p>
     * 一般用于比较重要的属性，比如停用启用，比如上下架
     * <p>
     * 即值得单独发一条的属性
     *
     * @param recordClass 记录类型，主要是扫描注解用的
     * @param which       哪个字段
     * @param id          记录id
     * @param from        从什么
     * @param to          变成什么
     * @param <C>         记录类型
     * @param <F>         字段类型
     */
    public static <C, F> void sendEntityFieldChangedEvent(Class<C> recordClass, FieldName which, String id, F from, F to) {
        sendEvent(scanTopic(recordClass), new FieldChanged(), EntityFieldChangedMessageTemplate.<F>builder().id(id).which(which.getName()).from(from).to(to).build());
    }

    /**
     * 关系属性变更事件
     * <p>
     * 一般用于比较重要的属性，比如停用启用，比如上下架
     * <p>
     * 即值得单独发一条的属性
     *
     * @param relationClass 记录类型，主要是扫描注解用的
     * @param which         哪个字段
     * @param relation      记录id
     * @param from          从什么
     * @param to            变成什么
     * @param <C>           记录类型
     * @param <F>           字段类型
     */
    public static <C, F> void sendRelationFieldChangedEvent(Class<C> relationClass, FieldName which, Map<String, String> relation, F from, F to) {
        sendEvent(scanTopic(relationClass), new FieldChanged(), RelationFieldChangedMessageTemplate.<F>builder().relation(relation).which(which.getName()).from(from).to(to).build());
    }
}
