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

用来过滤一个指定的java对象， 范型参数指明支持的对象类型。这样基于要过滤的对象的类型，可以编写符合业务需要的对象，比如

```java
public class User {
    private String name;
    private int level;
}

public class UserMinLevelCriteria implements JavaObjectCriteria<User> {
    private int level;

    public boolean meetCriteria(User object) {
        return object.getLevel() >= this.level;
    }
}
```

`UserMinLevelCriteria`会检查用户的最低等级是否符合预期。通过这种机制，配合"Trait"的编程理念，常见的过滤和条件标准还有希望得到进一步沉淀，如

```java

@Trait
public interface EnabledStatusFlag {
    boolean enabled;
}

public class User implements EnabledStatusFlag {
    private boolean enabled;
}

public class EnabledStatusCriteria implements JavaObjectCriteria<EnabledStatusFlag> {
    private boolean status;

    public boolean meetCriteria(User object) {
        return status == object.isEabled();
    }
}
```

`EnabledStatusCriteria`为所有实现`EnabledStatusFlag`的类型提供过滤能力

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

# 布尔与或非运算

## 过滤内存对象

* [BooleanCriteria](criteria-pattern%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Fpattern%2Fcriteria%2Fschema%2Fobject%2FBooleanCriteria.java)
* [MatchAllCriteria](criteria-pattern%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Fpattern%2Fcriteria%2Fschema%2Fobject%2FMatchAllCriteria.java)
* [MatchAnyCriteria](criteria-pattern%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Fpattern%2Fcriteria%2Fschema%2Fobject%2FMatchAnyCriteria.java)
* [NotCriteria](criteria-pattern%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Fpattern%2Fcriteria%2Fschema%2Fobject%2FNotCriteria.java)

以上类型均使用[JavaObjectCriteria](criteria-pattern%2Fsrc%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Fpattern%2Fcriteria%2Fschema%2Fobject%2FJavaObjectCriteria.java)
作为输入，构造与或非条件。比如


```java
public class User {
    private String name;
    private int level;
}

public class UserMinLevelCriteria implements JavaObjectCriteria<User> {
    private int level;

    public boolean meetCriteria(User object) {
        return object.getLevel() >= this.level;
    }
}

public class User {
    private String name;
    private int level;
}

public class UserNameNotEmptyCriteria implements JavaObjectCriteria<User> {

    public boolean meetCriteria(User object) {
        return StringUtils.hasText(object.getName());
    }
}

public class SampleApp {
    public static void main() {
        BooleanCriteria.builder()
                //条件a
                .a(new UserMinLevelCriteria(10))
                //并且
                .operator(AND)
                //条件b
                .b(new UserNameNotEmptyCriteria())
                .build()
                //判断一个100级，名字不为空的用户
                .meetCriteria(new User("not empty", 100));
    }
}
```