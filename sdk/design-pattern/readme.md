# 标准模式

过滤器模式（Filter Pattern）或标准模式（Criteria
Pattern）是一种设计模式，这种模式允许开发人员使用不同的标准来过滤一组对象，通过逻辑运算以解耦的方式把它们连接起来。这种类型的设计模式属于结构型模式，它结合多个标准来获得单一标准。

这个标准有2种用法

* 用于一组内存对象的循环，查看当前内存对象是否符合预期(比如过滤一组已经读取出来的用户，一组接口的实现等)
* 用来产生其它中间件所需的过滤语句，比如sql查询条件，es的查询条件等

# Criteria

`Criteria`是根接口，表达当前对象是一个标准模式的实现

# JavaObjectCriteria

```java
public interface JavaObjectCriteria<O> extends Criteria {
    /**
     * 当前对象是否满足标准
     *
     * @param object 需要过滤的对象
     * @return 是否满足要求
     */
    boolean meetCriteria(O object);
}
```

用来过滤一个指定的java对象， 范型参数指明支持的对象类型

# PersistenceQueryCriteria

```java
public interface PersistenceQueryCriteria<T> {
    /**
     * 构造成目标持久化引擎能够支持的查询语句
     *
     * @return 查询语句
     */
    T build();
}
```

用来构建一个查询数据引擎的表达式或者引擎sdk能够理解的java对象，这个对象一般来说会是个字符串(或者json形式的字符串)