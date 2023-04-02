# 标准化数据结构

"api-standard-data"工程定义了rest api使用的标准化数据结构定义

## ApiStandardDataTraits

`ApiStandardDataTraits`中封装了以下几个常用的标准化请求参数或响应的字段定义

* `Id`: 包含了传输过程中与资源的id有关的属性
* `Keyword`: 包含了查询关键词，推荐使用空格作为分隔符
* `Ids`: 包含了指定查询或操作的id清单
* `PageNo`: 包含了页码
* `PageMarker`: 包含了页签，也就是另一种形式的页码，比如瀑布流的页面加载一般使用页签
* `PageSize`: 包含了页大小
* `TotalNumber`: 包含了批量请求的总数，比如一个查询请求找到了100万条数据
* `Sort` & `Sorts`: 排序的列名(可选)以及升序还是降序的`order`属性。如果支持多个列进行排序，则使用`Sorts`
* `Contents`: 包含了批量请求或响应的内容，内容类型由范型参数决定

以上标准化的数据结构使用了接口的形式实现，这种实现方式有利于开发人员在参数/响应的类上自由组合实现的接口，并通过lombok的`Getter`/`Setter`
注解快速生成数据对象，例如查询用户的请求

```java

@Getter
@Setter
public class SearchUserParameter implements
        ApiStandardDataTraits.Keyword,
        ApiStandardDataTraits.PageNo,
        ApiStandardDataTraits.pageSize {
    @OptionalOrNotBlank
    private String keyword;
    @Positive
    private Integer pageNo = 1;
    @Positive
    private Integer pageSize = 10;

}
```

或导入用户的请求

```java

@Getter
@Setter
public class ImportUserParameter implements
        ApiStandardDataTraits.Contents<User> {
    @Valid
    private Collection<@NotNull User> contents = new ArrayList<>();
}
```

## 使用Validation注解

众所周知，spring支持validation注解，通过在请求方法的参数前加上`@Valid`注解就能使得spring自动基于参数属性上的其它验证注解完成参数校验，如下面的controller示例

```java

@RequestMapping("/user")
@RestController
@Component
public class UserEndpoint {
    @GetMapping
    public SearchUserResponse searchUser(
            @Valid SearchUserParameter searchUserParameter
    ) {
        //...
    }

    @PostMapping(":import")
    public ImportUserResponse importUsers(
            @Valid @RequestBody ImportUserParameter importUserParameter
    ) {
        //...
    }
}
```

<font color=red>注意</font>: `GET`请求参数上并没有`@RequestParam`
注解，这是因为不需要这个注解，spring也能将`GET /user?keyword=xxx&pageNo=1&pageSize=50`
中的各个请求参数正确的填入`SearchUserParameter`对象中

## 预定好的ApiStandardDataTraits实现

为了加速开发过程，防止开发过程中开发人员遗漏对参数等对象的常用验证注解，在"api-standard-data"中包含了上文所有标准化参数属性的预定义实现

* `GenericContents`: 通用的内容清单类，要求每一个内容不能为null且进行内部验证通过；同时，如果内容清单为null。则返回一个空列表，而不是null
* `GenericStringId(s)`: 通用的，以字符串作为id类型的类，对于GenericStringId，要求如果提供id的值则不能为空字符串；
  对于GenericStringIds，要求给定的每一个id都不能是null或者空白字符串
* `GenericPageNo` & `GenericPageMarker`: 通用的页签和页码内容，页签要是如果提供了值则不能是空白字符串，页码要求是正数，当页码的值为null时，默认返回1
* `GenericSort` * `GenericSorts`: 通用的排序内容，同样要求排序列如果提供了则不能是空，顺序默认为升序。多列排序要求每一个排序元素不能为null且通过了元素自己的所有验证要求
* `GenericPageSize`: 通用的一页请求大小，要求必须是个正数，由GenericNaxPageSizeProvider的实现类bean来提供页码的最大值,
  接口有一个默认实现DefaultGenericNaxPageSizeProvider，返回值为50

因此，<font color=red>简单的</font>
情况下，开发人员可以直接使用这些已经预定义好的参数，但是由于java的单类继承问题，无法完成类似`SearchUserParameter`的组合效果。
这时，开发人员可以巧妙的利用lombok组件的`@Delegate`注解来实现自由组合的目标，如下所示

```java

public class SearchUserParameter implements
        ApiStandardDataTraits.Keyword,
        ApiStandardDataTraits.PageNo,
        ApiStandardDataTraits.pageSize {
    //通过委派模式
    @Delegate
    @JsonIgnore
    @Valid
    private final Keyword keyword = new GenericKeyword();
    //通过委派模式
    @Delegate
    @JsonIgnore
    @Valid
    private final PageNo pageNo = new GenericPageNo();
    //通过委派模式
    @Delegate
    @JsonIgnore
    @Valid
    private final PageSize pageSize = new GenericPageSise();
}
```

这样，每一个参数的getter和setter就通过委派模式进行对外的暴露，同时享受了预定义的验证逻辑。

## 标准化的api错误数据实体

`ApiError`定义了基本的api错误标准数据格式，包含了以下几个主要组成部分

* 调用上下文: 主要是`时间`，被调用的`路径`以及`响应状态`(显然状态理应是4xx和5xx)
* 错误信息: 主要是错误类型的`识别符号`，错误`消息`
* 处理提示: 当前的保留字段
* 详情: 一个额外的Map<String, Object>字段，用来让报错方设置自定义的错误详细信息，比如: 用户因密码错误次数过多导致被封锁，详情则可以附上封锁的解除日期等

# 应用程序的错误格式统一

"api-standard-error"工程实现对了应用程序错误格式的标准格式化输出，它内含了"api-standard-error-exceptions"的内容

## ApiErrorFactory

从spring mvc的原理出发，所有错误最终都到达"/error"这个地址，由`BasicErrorController`完成输出任务，`BasicErrorController`
调用一个叫`ErrorAttributes`
的接口将具体的请求和问题转为Map类型的映射。在这基础上，组件首先提供了一个`ServletApiErrorAttributes`
作为api接口报错的统一处理入口，然后由这个标准化的处理器调用`ApiErrorFacotry`来完成默认错误属性向标准错误的转换。

```java
public interface ApiErrorFactory {
    /**
     * 创建api错误对象
     *
     * @param errorAttributes 错误属性
     * @param error           捕捉到的错误
     * @param locale          本地化上下文
     * @return 转换的错误
     * @see org.springframework.boot.web.servlet.error.ErrorAttributes
     * @see org.springframework.boot.web.reactive.error.ErrorAttributes
     */
    ApiError createApiError(Map<String, Object> errorAttributes, @Nullable Object error, Locale locale);
}
```

参数中

* errorAttributes是由`ErrorAttributes`转完的，spring原始的错误数据
* error是捕捉到的错误对象，这里注意并没有强制认为错误是一个Exception，理由是很多报错其实是`Response.sendError`来发送的，压根没有异常。
* locale是当前请求的语言环境

## DefaultApiErrorFactory

上文接口的一个默认实现，使用的是事件机制来完成错误向标准化错误的转换

### InitializingApiErrorPropertiesEvent

由默认工厂发送给监听器，要求监听器自行设置标准错误的属性

### InheritPropertiesFromErrorAttributesListener

这个监听器把spring的报错属性中的路径，http编码等属性写入到标准化错误中

### InitializingWithGenericErrorListener

这个监听器首先将报错的编码设置为一个默认的兜底编码，这样当其它监听器没有设置错误编码时，不至于导致没有错误编码出现

### ResponseStatusAnnotationAwareListener

这个监听器检查错误是否包含了`@ResponseStatus`注解，如果包含则按照注解的值设置http状态码

### ErrorRevealHandlingListener

这个监听器检查错误是否包含在开发人员指定的业务错误包内，如果在则将类名作为错误码以及设置正确的错误信息，否则不去处理

#### RevealError & HideError

这两个注解实现的就是非业务错误的展示或屏蔽，比如mybatis的底层报错，redis的底层报错等，不会因为没有处理而直接暴露给使用方。
因此，开发人员需要将自己的业务错误暴露出去时，或者

* 在某个配置类上注解`RevealError`配置错误类所在的包(或基类)
* 或者在需要需要暴露的错误类上直接注解`RevealError`

否则输出的总是`DefaultApiErrorConstants.generic`，也就是一个较为宽泛的描述

如果要明确隐藏某个错误，则使用的是`HdieError`注解

错误输出之前的判断优先级为

* 检查错误类上是否直接注解了`RevealError`或`HideError`
* 检查错误类是否在`HideError`标注的包或基类中
* 检查错误类是否在`RevealError`标注的包或基类中

### NullErrorObjectHandlingListener

这个监听器处理错误为null的场景，它基于http状态码将错误编码映射为组件内封装的标准http状态异常类

### CommonSpringWebExceptionHandlingListener

这个监听器将一些spring的常见web异常映射为组件内封装的标准http状态异常类，这样使得这些异常不需要配置为业务错误就能输出，否则将显示上面`InitializingWithGenericErrorListener`
的一个默认的兜底报错，不符合设计预期

### BadRequestArgumentRegulationListener

主要处理spring的验证注解抛出的`BindException`，将它转为统一的`BadRequestArgumentException`并设置错误参数的详情

### ApiErrorDetailsSupplierHandlingListener

如果错误实现了`ApiErrorDetailsSupplier`接口，则按照接口输出错误的详情

以上监听器的默认调用顺序都是`Order(0)`，如果需要在之前插入，则监听器的调用顺序 < 0 即可，反之需要 > 0

### 转换结果

通过以上监听器，实现的结果是

* 使用异常的类路径作为`ApiError`
  字段的编码，比如应用程序抛出了`com.jdcloud.gardener.fragrans.api.standard.error.exception.client.GoneException`
  ，则对应的响应结果就变成了

```json
{
  "timestamp": "2022-08-22T11:50:16.017+00:00",
  "status": 410,
  "reason": "Gone",
  "uri": "/controller/exception",
  "error": "com.jdcloud.gardener.fragrans.api.standard.error.exception.client.GoneException",
  "message": "资源已被永久移除",
  "hint": null,
  "details": null
}
```

* 基于异常的编码，在i18n国际化文件中搜索编码对应的国际化文本(有关这部分内容，参考"enhance-message-source"工程的介绍)
* 如果异常实现了`ApiErrorDetailsSupplier`接口，则按照接口的定义

```java
public interface ApiErrorDetailsSupplier {
    /**
     * 给出详情
     *
     * @return 详情
     */
    Map<String, Object> getDetails();
}
```

将获取的详细信息放到`details`字段中(于是报错就可以携带一些通用信息)

## 标准化异常

为了加速开发过程，"api-standard-error-exceptions"封装了符合http状态码的标准化的错误异常，开发人员可以选择从这个标准化的异常进行继承，比如

```java
public class BadRequestException extends RuntimeException {
    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }

    public BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
```

这个异常会基于ApiErrorFactory的默认实现转为http 400。其它标准化异常参考"
com.jdcloud.gardener.fragrans.api.standard.error.exception"
包中的异常定义，基本包含了所有http错误状态

此外，作为标准化api错误的调用房，可以单独引用"api-standard-error-exceptions"，然后基于`ApiError`对象中的`error`属性做出如下判断

```java
public class Sample {
    /**
     * 基于错误代码判断是否是某个给定异常
     * @param apiError 错误
     * @param target 异常类
     * @return true - 是 / false - 否
     */
    public boolean isException(ApiError apiError, Class<? extends Exception> target) {
        return target.getCanonicalName().equals(apiError.getError());
    }
}
```

这时，可以使用标准异常的类名进行判断，从而做到如果标准异常的类路径或名称发生变化，代码编译时有感

<font color=red>警告</font>: "api-standard-error-exceptions"引用了"enhance-message-source"
，后者会自动生成一个`MessageSource`
的bean，使得原应用程序中如果开发人员声明了自己的`MessageSource` bean并进行依赖注入时，可能会错误地注入由"
enhance-message-source"生成的。
要解决这个问题，推荐原来注入`MessageSource`的位置加上`@Qualifier`注解。之所以这么做，是因为错误代码的国际化输出依赖于"
enhance-message-source"的功能。<font color=red>
实际上</font>大部分工程编写的时候根本也不生成什么`MessageSource`进行i18n国际化

## 配合 spring security 使用

目前主要针对spring boot security的兼容，在配合spring security使用时，会通过`WebSecurityCustomizer`类的bean来放开`/error`
路径以防错误无法正常输出

## 小结

通过"api-standard-error"组件，开发人员在需要中断程序的时候直接抛出带有`@ResponseStatus`或继承了带有该注解的类的异常，组件自动完成

* http状态码转换
* 标准化`ApiError`的格式化
* 配合i18n完成错误消息的国际化输出