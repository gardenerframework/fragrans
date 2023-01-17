# 引言

大量的应用在数据库记录发生变动或其它事件需要发送领域时间进行通知，本工程就是用来规定事件的基本格式和发送统一接口

# DomainEvent

这个类规定了事件的基本格式，包含

```java
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
```

* id是事件的识别符号，用来追踪事件的发送和接受的
* type是事件的类型
* payload是事件的负载

## EventType

接口用来表达事件的类型，其机制是将实现类的类名用大写+下划线的形式作为类型使用，包含

* CreateRecord
* DeleteRecord
* FieldChanged
* UpdateRecord

## UpdateMessageTemplate

对于创建和删除记录，发送的事件都可以直接是对象，而对于更新，需要知道从什么更新为什么，于是统一抽象了更新消息模板

```java
public class UpdateMessageTemplate<O> {
    private O from;
    private O to;
}
```

## EntityFieldChangedMessageTemplate & RelationFieldChangedMessageTemplate

对于只更新了一个字段的情况，增加抽象一个类型用于指示更了什么什么字段，以及从什么更新到了什么

```java
public class EntityFieldChangedMessageTemplate<F> extends UpdateMessageTemplate<F> {
    /**
     * 记录id
     */
    private String id;
    /**
     * 那个属性
     */
    private String which;
}

public class RelationFieldChangedMessageTemplate<F> extends UpdateMessageTemplate<F> {
    /**
     * 关系id
     */
    private Map<String, String> relation;
    /**
     * 那个属性
     */
    private String which;
}
```

作为关系类不难发现，关系的id是映射的kv对，k = id的名称，如用户名，v是关系id的值，如"zhanghan30"

## OperationTraceableChangedField

部分属性值的更细要求附加谁更新的，这个类就是在值的基础上增加了更新人

```java
public class OperationTraceableChangedField<F> {
    private F value;
    private String updater;
    private Date lastUpdateTime;
}
```

## @Topic

用来给记录类上附加要发送到那个消息队列的注解

## FieldName

用于`EntityFieldChangedMessageTemplate`，同样是类名的大写+下划线，用来指示哪个字段发生变化

## DomainEventHelper

提供数据库记录变化的基本事件发送接口和原生事件的发送接口

## DomainEventSender

用来和消息队列耦合的插件

```java

@FunctionalInterface
public interface DomainEventSender {
    /**
     * 发送领域事件
     *
     * @param topic 事件主题
     * @param event 事件
     */
    void sendEvent(String topic, DomainEvent event);
}
```

