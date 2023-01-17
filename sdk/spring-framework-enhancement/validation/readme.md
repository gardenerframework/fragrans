# 引言

应用程序对输入参数、运行环境、系统返回无一不需要进行验证以确保处理符合预期。 本工程在此就在java的`Validator`上进行增强，补充常用的验证场景.。 此外本项目还生成`LocalValidatorFactoryBean`
，使用消息源来统一整个错误信息编码的转义工作(也就是可以认为enhance-message-source组件生成的消息源能够对验证错误消息生效)

# 使用统一消息源

本组件自动引入"enhance-message-source"组件，使用该组件生成的消息源作为参数错误的消息国际化支持组件。这意味着开发人员可以通过统一的体验和方式将参数错误文本预设到项目中

# 验证注解类即消息编码

java的验证注解中有一个关键的字段是消息的编码，这个编码是个字符串。从最佳实践来看，这个字符串的命名也是各种花样。本着之前的使用经验和需求，命名类型时，其所属的包和类的名称如果是精心设计的，那么没有必要再新增一套命名体系。
因此本组件就也实现了一个基础的验证器，这个验证器做的就是将消息编码默认写成类的全路径。 若需要使用这个功能，开发人员自己定义的验证器需要继承`AbstractConstraintValidator`，这个类里面的两个方法需要关注

```java
public abstract class AbstractConstraintValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {


    /**
     * 子类实现具体的验证方法
     *
     * @param value   值
     * @param context 上下文
     * @param data    数据字段，用于实现类自己存储一些验证过程中的数据，这些数据的设计初衷是给构建错误消息时使用的
     * @return 是否合法
     */
    protected abstract boolean validate(T value, ConstraintValidatorContext context, Map<String, Object> data);


    /**
     * 构建参数错误消息，其实主要是往里面填参数
     *
     * @param value 值
     * @param data  自己在验证时存的数据
     */
    @Nullable
    protected Map<String, Object> getMessageParameters(T value, Map<String, Object> data) {
        return null;
    }
}
```

* validate方法负责执行验证
* getMessageParameters方法负责向消息模板(类路径)中填参数(当前要求实现了Hibernate验证器)

# 增加常用的验证注解

## 集合类型

组件为集合类型添加了以下几个注解

* DistinctItem: 要求集合内部的元素不得重复(要求元素override了equals和hashcode)

## 映射类型

组件为映射类型提供了以下几个注解

* OneToOneMapping: 要求映射中的元素为1:1映射

其余OneToZeroOrOne(值包含null), ManyToOne映射模式为映射的天然支持，而OneToMany在映射中不存在(无法存储多个重复的key，需要使用MultiValueMap)

## 文本类型

组件为字符串类型提供了以下几个注解

* OptionalNonBlank: 字符串可以为null，但是一旦提供就不能是空白
* UpperUnderscore: 字符串需要是大写+下划线的模式(但不能以下划线开头)
* LowerUnderscore: 字符串需要是小写+下划线的模式(但不能以下划线开头)

## 数据范围

常规的Max、Min等注解的大小是写死的，而大部分情况下这类验证结果需要动态获取，比如数据传输的最大长度，一次性访问允许的页面大小等。因此本组件扩展了这些基本的范围注解

* Max: 给定的参数不能超过指定值
* Min: 给定的参数不得小于指定值
* Range: 给定的参数范围在指定值内

当使用数据范围时，通常需要指定`MinConstraintProvider`和`MaxConstraintProvider`，这两个接口一个负责动态的给出最小值，一个给出最大值

这样就可以实现类似下面的功能

* 接口1要求的最大页面大小和接口2不一样，于是两个接口的分别是

```java
class Page1 {
    @Max(provider = PageSizeProvider1)
    private Integer size;
}
```

```java
class Page2 {
    @Max(provider = PageSizeProvider2)
    private Integer size;
}
```

各自实现接口就可以了。如果两个要使用同样的基类，则这样定义

```java
class PageBase {
    //不要有任何注解
    private Integer size;
}
```

```java
class Page1 extends PageBase {
    @Max(provider = PageSizeProvider1)
    private Integer size;
}
```

```java
class Page2 extends PageBase {
    @Max(provider = PageSizeProvider2)
    private Integer size;
}
```

虽然看起来好像没啥意义就是了，这样蹩脚的原因是不允许子类修改父类的验证注解，理由是如果子类改了父类的注解，那么父类的实现预期就被修改了，可能造成未知问题且很难查证。 所以就得这样属性隔离。
此外，子类如果重新定义了一个父类的同名属性，父类的属性也依然在进行验证，比如父类要求某个属性不能为null，子类虽然用了一个同名属性看起来覆盖了父类的属性，但是实例化之后，父类的属性因为是null(假设未赋值)而会说不符合验证规则。

从最佳实践上看，最好是把所有属性定义都放到接口上，然后记录类来实现接口从而约定数据的名称和类型，当然这个就有点反人类了。

因此，这个玩意怎么用，就仁者见仁了