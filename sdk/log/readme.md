# 引言

日志是程序中的必备功能，spring和log4j也提供了很好的日志记录的底层支持。为了进一步规范化日志的记录，特别在这些基础上再进行上层抽象，是的日志记录能够

* 遵守预定义的格式
* 使用规范化的，程序可解析本文
* 通过事件机制使得能够被异步监听，从而和审计系统等联动在一起，降低开发劳动量

# 格式化消息

观察log4j的日志结构

```log
2014-07-02 20:52:39 DEBUG HelloExample:19 - This is debug : mkyong
```

其中时间、打印的类等已经都被格式化好了。而消息，也就是message的部分被预设为一行纯文本。 为了使得消息的部分也能被程序解析，不少人将日志改造成了json的格式，且网上有大量的教程来教你怎么进行格式改造。
这能解决一部分message的格式化问题，但需要面对一个不好的地方: json格式的日志读起来很麻烦

```json
{
  "@timestamp": "2017-05-25T19:56:23.370Z",
  "ecs.version": "1.2.0",
  "log.level": "ERROR",
  "message": "Hello, error!",
  "process.thread.name": "main",
  "log.logger": "org.apache.logging.log4j.JsonTemplateLayoutDemo",
  "error.type": "java.lang.RuntimeException",
  "error.message": "test",
  "error.stack_trace": "java.lang.RuntimeException: test\n\tat ...\n"
}
```

这不太符合运维人员的阅读习惯，特别是异常的部分。 因此，需要一种折中的方法里使得保持文本的阅读性，同时使得程序有一定手段能够去解析或转成所需的形式化描述的格式

# 消息语句拆分

消息在现实世界中就是一句话(或几句话)，它由若干个词构成。因此拆分消息语句得到了类型`Word`

```java
public interface Word {

}
```

词是一个抽象接口，它的实现类直接实现`toString`方法来将它的含义转为文字。有了词之后，消息通过`Template`将词连在一起。

```java
public interface Template {

}
```

模板同样需要实现`toString`方法，给出一个符合slf4j的日志格式定义的日志模板，比如"{}{}{}"，这意味着这条日志将包含3个词。

# BasicLogger

在这样思路的引导下，`BasicLogger`提供了日志的debug、info、warn、error的基本书写功能，并在日志成功写入后发送`LogEvent`
事件。它是一个bean，开发人员在需要时引入这个bean。该bean在声明时带有`@Primary`注解，因此如果需要其它子类，则正确使用子类类型来进行依赖注入

<font color=orange>需要注意</font>: `BasicLogger`
机制可能造成一些开发上的不便，使得日志在写入前首先需要对所有词和模板进行预设从而降低了开发速度。但这不正是本组件的设计目标吗？如果要随心所欲的写日志，那也不需要使用本组件是不是

# 通用模板

log-generic-* 组件包提供了一些通用的日志操作模板和数据定义。这种日志的顶层模板由"主要日志信息"、"详细日志信息"和"操作方"(可选)组成，如

```log
完成更新用户缓存[key=user123, expiresAt=10:00], 操作方: [user=管理员, client=手机端]
```

其中:

* 完成更新用户缓存是称作主要日志信息
* [key=user123, expiresAt=10:00]则是详情
* 操作方的部分本文觉得就不需要讲解了

这种模板的设计理念是

* 将最主要的事放到"主要日志信息"中并尽可能简洁
* 和日志相关的详细原因在"详细日志信息"中

比如"缓存已消失，重新写入用户id为123的缓存"这句话在以上的设计理念下就是

```log
完成设置用户缓存[key=user123, expiresAt=10:00, reason=cache missing], 操作方: [user=管理员, client=手机端]
```

即心思不用放在组织语言上(组织人话从不是程序员擅长的事情)，让主要日志信息简洁地表达问题，详细信息列举所需的字段

## 常规基础日志

常规基础日志就是由2个主词，1个详情构成，即什么发生了什么，用例比如有

* "用户(什么)登录(发生了什么)[userId=123, name=张三, ip=1.1.1.1]"
* "参数(什么)不合法(发生了什么)[parameterName=username, violation=@NotBlank]"

等等这种非常简单的日志，也就是2个词+1个详情能说清楚的事，2个词构成主要日志信息，这个模板一般不需要操作方。开发人员按需引入`GenericBasicLogger`的bean。

## 常规操作日志

常规操作日志由3个主词，1个详情和1个操作方构成，即什么东西被进行了什么操作最后怎么样了，用例比如有

* "商品(什么东西)添加(被进行了什么操作)成功(最后怎么样了)[spuId=123, name=球鞋, color=红色, 尺码=28码], 操作方: [user=商户A, client=商户端]"
* "用户(什么东西)登录(被进行了什么操作)失败(最后怎么样了)[userId=123, name=张三, reason=需要进行mfa多因子验证], 操作方: [user=商户A, client=商户端]"

开发人员按需引入`GenericOperationLogger`的bean。

# 常用词

既然日志由模板和词构成，那么log-common-words就给了一些非常常见的词，包含了

* 数据的发送、接收，增删改查；
* 接口的的请求，处理，响应；
* 资源的锁定，释放
* 任务的启动，停止

等，开发人员按需使用

# 词语抽象导致的类型爆炸

当所有词都需要用一个类型去表达的时候，java就需要加载无数个类来表达每一个日常词汇。为了解决类型爆炸的问题，组件提供了`SimpleWord`类，这个类要求输出一个字符串，最终也输出这个字符串

# 消息发送

最后，日志记录的时候，可以通过`enableLogEvent`为单独的logger(实际为logger.getName())设置是否写日志时发送日志事件`LogEvent`

```java
public class LogEvent {
    /**
     * 日志记录类的名称
     */
    private final String loggerName;
    /**
     * 使用的日志模板
     */
    private final Template template;
    /**
     * 所有词汇
     */
    private final Collection<Word> words;
    /**
     * 抛出的异常
     */
    @Nullable
    private final Throwable cause;
}
```

# 使用注意

将日志格式化，形式化描述与日志书写的随意与灵活之间存在着取舍关系。本组件的意义是将日志的消息格式进行形式化描述，并通过应用事件机制与审计监听器，自动化运维等手段进行联动和定制。 过度抽象日志词汇就加大研发劳动量，降低工作效率。