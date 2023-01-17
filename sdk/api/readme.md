# 引言

api-xxx组件集提供了一系列编写api接口的工具和标准化定义

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

以上标准化的数据结构使用了接口的形式实现，这种实现方式有利于开发人员在参数/响应的类上自由组合实现的接口，并通过lombok的`Getter`/`Setter`注解快速生成数据对象，例如查询用户的请求

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
注解，这是因为不需要这个注解，spring也能将`GET /user?keyword=xxx&pageNo=1&pageSize=50` 中的各个请求参数正确的填入`SearchUserParameter`对象中

## 预定好的ApiStandardDataTraits实现

为了加速开发过程，防止开发过程中开发人员遗漏对参数等对象的常用验证注解，在"api-standard-data"中包含了上文所有标准化参数属性的预定义实现

* `GenericContents`: 通用的内容清单类，要求每一个内容不能为null且进行内部验证通过；同时，如果内容清单为null。则返回一个空列表，而不是null
* `GenericStringId(s)`: 通用的，以字符串作为id类型的类，对于GenericStringId，要求如果提供id的值则不能为空字符串；
  对于GenericStringIds，要求给定的每一个id都不能是null或者空白字符串
* `GenericPageNo` & `GenericPageMarker`: 通用的页签和页码内容，页签要是如果提供了值则不能是空白字符串，页码要求是正数，当页码的值为null时，默认返回1
* `GenericSort` * `GenericSorts`: 通用的排序内容，同样要求排序列如果提供了则不能是空，顺序默认为升序。多列排序要求每一个排序元素不能为null且通过了元素自己的所有验证要求
* `GenericPageSize`: 通用的一页请求大小，要求必须是个正数，由GenericNaxPageSizeProvider的实现类bean来提供页码的最大值,
  接口有一个默认实现DefaultGenericNaxPageSizeProvider，返回值为50

因此，<font color=red>简单的</font>情况下，开发人员可以直接使用这些已经预定义好的参数，但是由于java的单类继承问题，无法完成类似`SearchUserParameter`的组合效果。
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

从spring mvc的原理出发，所有错误最终都到达"/error"这个地址，由`BasicErrorController`完成输出任务，`BasicErrorController`调用一个叫`ErrorAttributes`
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

这两个注解实现的就是非业务错误的展示或屏蔽，比如mybatis的底层报错，redis的底层报错等，不会因为没有处理而直接暴露给使用方。 因此，开发人员需要将自己的业务错误暴露出去时，或者

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

* 使用异常的类路径作为`ApiError`字段的编码，比如应用程序抛出了`com.jdcloud.gardener.fragrans.api.standard.error.exception.client.GoneException`
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

这个异常会基于ApiErrorFactory的默认实现转为http 400。其它标准化异常参考"com.jdcloud.gardener.fragrans.api.standard.error.exception"
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

<font color=red>警告</font>: "api-standard-error-exceptions"引用了"enhance-message-source"，后者会自动生成一个`MessageSource`
的bean，使得原应用程序中如果开发人员声明了自己的`MessageSource` bean并进行依赖注入时，可能会错误地注入由"enhance-message-source"生成的。
要解决这个问题，推荐原来注入`MessageSource`的位置加上`@Qualifier`注解。之所以这么做，是因为错误代码的国际化输出依赖于"enhance-message-source"的功能。<font color=red>
实际上</font>大部分工程编写的时候根本也不生成什么`MessageSource`进行i18n国际化

## 配合 spring security 使用

目前主要针对spring boot security的兼容，在配合spring security使用时，会通过`WebSecurityCustomizer`类的bean来放开`/error`路径以防错误无法正常输出

## 小结

通过"api-standard-error"组件，开发人员在需要中断程序的时候直接抛出带有`@ResponseStatus`或继承了带有该注解的类的异常，组件自动完成

* http状态码转换
* 标准化`ApiError`的格式化
* 配合i18n完成错误消息的国际化输出

# 接口切面简化

在部分情况下开发人员需要对api接口进行全局的切面来完成一些操作，比如计算每一个接口的调用时间，或者基于参数的注解完成一些全局操作，于是"api-advice-engine"就引入并实现这种需求

## EndpointHandlerMethodAdvice接口

```java
public interface EndpointHandlerMethodAdvice {
    /**
     * 在controller的端点方法执行前
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void before(Object target, MethodSignature methodSignature, Object[] arguments) throws Exception {
    }

    /**
     * 在controller的端点方法执行后
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @param returnValue     返回值
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void after(Object target, MethodSignature methodSignature, Object[] arguments, @Nullable Object returnValue) throws Exception {
    }

    /**
     * 在捕捉到异常时
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @param exception       捕捉到了什么异常
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void fail(Object target, MethodSignature methodSignature, Object[] arguments, Exception exception) throws Exception {

    }
}
```

实现这个接口并声明为bean，这样当controller的方法触发时就会按照接口的说明分别调用`before`、`after`和`fail`

## EndpointHandlerMethodBeforeAdviceAdapter

这个接口使得开发人员可以实现一个和要切入的controller方法的声明一样的方法，当controller的方法发动时，引擎会匹配被调用的方法在子类中是否存在，如果存在就调用否则忽略

## 用例: 通过切面判断用户权限

* 第一步: 编写controller类

```java
/**
 * 首先定义接口骨架(其实可以用idea的refactor功能从controller类抽取接口完成)
 */
public interface UserManagementEndpointSkeleton {
    /**
     * 作为管理员创建用户
     * @param createUserParameter 参数
     * @return 创建结果，包含用户的id
     */
    CreateUserResponse createUser(@Valid CreateUserParameter createUserParameter);
}

@RequestMapping("/user")
@RestController
@Component
public class UserManagementEndpoint implements UserManagementEndpointSkeleton {
    @Override
    @PostMapping
    public CreateUserResponse createUser(CreateUserParameter createUserParameter) {
        //完成用户创建
        String userId = userService.createUser(modelMapper.map(createUserParameter, User.class));
        //返回结果
        return new CreateUserResponse(userId);
    }
}
```

从上面的接口可以看出，`createUser`方法中没有任何权限判断逻辑

* 第二步，编写切面

```java

@Component
public class UserManagementEndpointSecurityHandler extends EndpointHandlerMethodBeforeAdviceAdapter implements UserManagementEndpointSkeleton {
    @Override
    public CreateUserResponse createUser(CreateUserParameter createUserParameter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal principal = authentication.getPrincipal();
        if (!principal.getRole.contains("ADMIN")) {
            throw new ForbiddenException("user " + principal.getUsername() + " is not ADMIN role");
        }
    }
}
```

这样，在调用controller的创建用户前，就会先调用切面的同名方法。这种模式显然有利于提高代码的可维护性。

# 增强验证

目前spring在get方法不识别@Valid注解，"api-enhance-validation"就负责解决这个问题，引入这个包后就能在路径参数，get参数前加@Valid和需要的验证注解

# 接口分组

"api-group-xxx"提供了为开发人员进行接口分组的能力

## 分组的意义

开发人员在开发api接口的时候会有这样的模式

* 对接使用方的接口是一组，统一前缀是/api
* 对接监控系统的接口是一组，统一前缀是/monitoring
* 对接运营后台的接口是一组，统一前缀是/management

这些接口由不同的人开发，有些可能开发完成后相关人也离职了，这时当需要实现以上的需求时，则可能需要在各个应用前面再假设一个nginx反向代理并逐个设置代理规则和统一前缀。

于是就有一种需求希望能够实现在源代码层面或运行时层面将已有接口按需编组并设置一些组策略

## 组与组策略

在"api-group-core"中，为开发人员定义了分组相关的基本数据结构和接口，其中组策略`ApiGroupPolicy`
是一个空的接口，这个接口标记相关的数据结构是组的策略，不同的分组业务目标可以自行定义自己的组策略。这样，使得分组与组策略的代码可以分离。

### ApiGroupProvider

一个api组的核心就是名称和成员，组的名称不能彼此重复，成员之间在目前版本下不能有交集

```java
public interface ApiGroupProvider {
    /**
     * 返回分组对应的注解
     *
     * @return 注解类
     */
    Class<? extends Annotation> getAnnotation();

    /**
     * 给出组成员
     *
     * @return 成员清单
     */
    Collection<Class<?>> getAdditionalMembers();
}
```

接口要求

* 说明分组是基于哪个注解进行的，使用注解类作为分组的识别符号
* 说明分组还包含哪些额外的成员，这些程序可能因为某些原因无法添加注解

### ApiGroupPolicyProvider

组定了之后就需要组策略

```java
public interface ApiGroupPolicyProvider {
    /**
     * 对应的分组注解
     *
     * @return 注解类
     */
    Class<? extends Annotation> getAnnotation();

    /**
     * 返回对应的组策略
     *
     * @return 策略
     */
    ApiGroupPolicy getPolicy();
}
```

这个接口负责提供组策略，它给出策略对应的分组注解以及策略的具体实例

## 使用

* 定一个分组用的注解，比如`ExampleGroup`
* 注解到分组所有Controller的类上
* 实现`ApiGroupProvider`的接口声明成bean，返回`ExampleGroup.class`
* 基于不同的业务需要，实现`ApiGroupPolicyProvider`来注册组策略
* 在业务代码中使用`ApiGroupRegistry`按策略类型或注解类型获取成员，完成成员的统一设置或操作

## 基于分组设置接口统一前缀

"api-group-context-path"实现了`ApiGroupContextPathPolicy`，用于设置每一个分组的统一前缀，比如

```java

@RequestMapping("/a")
@ExampleGroup
public class ControllerA {

}

@RequestMapping("/b")
@ExampleGroup
public class ControllerB {

}

public class ExampleGroupProvider implements ApiGroupProvider, ApiGroupPolicyProvider<ApiGroupContextPathPolicy> {
    /**
     * 对应的分组注解
     *
     * @return 注解类
     */
    public Class<? extends Annotation> getAnnotation() {
        return ExampleGroup.class;
    }

    /**
     * 返回对应的组策略
     *
     * @return 策略
     */
    public ApiGroupContextPathPolicy getPolicy() {
        return new ApiGroupContextPathPolicy("/api");
    }
}
```

这样之后，`ControllerA`和`ControllerB`的路径就会加上"/api"，从而变成了"/api/a"和"/api/b"(之前是"/a"和"/b")
。相当于对原有@RequestMapping中能够使用el表达式作为统一路径的增强，即通过程序的方式而不是开发人员感知的方式设置统一路径

# 安全与审计

"api-security-operator-xxx"用来补充api的安全审计功能

## OperatorDetails

`OperatorDetails`是由本组件生成的一个scope=request的bean，它主要在请求中提供有关操作人的详细数据，包含

* 操作客户端的id
* 客户端的角色(字符串集合)
* 客户端的权限(字符串集合)
* 操作人的id
* 操作人的角色(字符串集合)
* 操作人的权限(字符串集合)

这些常规数据来协助业务逻辑获取当前的操作人

当任何在请求过程中需要操作人详情的场景下，对比以往的参数传递，都推进直接`@Autowire OperatorDetails`来获得操作人的bean

<font color=orange>警告:</font> 需要注意，这个bean只在servlet的线程内生效，无法向其它线程进行传递。如果需要传递，则线程的执行方法应当获取一份当前对象的拷贝

## 与log-generic自动配合

log-generic提供了记录带有操作人的日志的功能，于是为了简化日志记录过程中与本组件的配合，api-security-operator-auto-logging会使用aop拦截`GenericOperationLogger`
的方法执行并自动注入操作人信息。同时，为了避免无意中修改了程序开发人员的设定值，这样的行为仅在操作人参数为`null`时生效

# 接口选项托管

大部分api接口都提供需要持久化保存的配置项，注意这里的配置项和配置文件不一样。比如

```yaml
server:
  port: 80
```

这是一个配置项，但是更应该放在配置文件中，而其他的业务相关的选项则更适合保存在数据库等位置

## 选项的注册

`@ApiOption`注解用于定义一个api选项，被定义的选项需要将自己声明为bean，选项的id就是bean name。此外，注解提供readonly属性，表明选项是一个只读的数据。 只读选项是真实存在的，比如当前服务器的加密公钥。
选项除去id外还有名称和版本号属性

```java
public class ApiOptionRegistryItem {
    /**
     * 具体的选项内容
     */
    private Object option;
    /**
     * 选项的名字
     */
    private String name;
    /**
     * 是否只读
     */
    private boolean readonly;
    /**
     * 版本号
     */
    @Nullable
    private String versionNumber;
}
```

名称是一个具备可读和描述性的字符串，默认情况下会使用选项的类路径作为message code，调用`EnhancedMessageSource`
去获取对应的文本。版本号则用于api具有多副本时的场景，主要是比较当前的版本号和持久化后的版本号是否一致，不一致则认为当前版本已经过期

## 选项初始化

当选项注册后，它处于未初始化的状态，也就是还没有从持久化系统中读取出之前的选项值，其当前值全部都是默认值，因此此时直接使用选项内容则可能造成问题

对此，`ApiOptionInitializer`
负责对选项进行初始化工作，它的方式是读取应用上下文中所有带有ApiOption注解的bean，然后逐一从持久化系统中读取出数据进行设置。

此外，`ApiOptionsEngineConfiguration`对所有带有`ApiOption`注解的bean的定义增加要求依赖`ApiOptionInitializer`初始化的指令。

在这样机制的保证下，基本所有选项都会晚于`ApiOptionInitializer`才会生成为bean，于是基本解决了选项未经初始化就直接被使用的问题

## 选项查询

"api-options-engine" 提供了选项查询接口`ApiOptionsEndpoint`，其默认监听"/options"，并整体返回所有已经注册的选项。当然如果知道了选项的id，也能使用单个id查询

## 选项更新

选项目前只支持对给定id的覆盖更新，开发人员需要调用`PUT /options/{id}`来更新一个选项，被更新的选项

* 首先调用validator对选项变更后的预览值进行校验，如果不符合验证要求则会中断逻辑，抛出`InvalidApiOptionException`
* 验证成功后发送`ApiOptionChangedEvent`事件，由监听器进行进一步处理
* `ApiOptionUpdater`最先监听以上应用事件，尝试进行持久化，如果持久化服务抛出异常则逻辑会被中断退出
* 其中一个比较特别的异常就是`ApiOptionVersionNumberConflictException`，这个的意思是当前选项使用的版本号已经和数据库中的不一致，即数据已经在别处被更新

## 选项持久化

选项的持久化由`ApiOptionPersistenceService`来完成，目前给出的是基于mysql数据库的版本"api-options-persistence-database"，它会要求固定读写"api-option"表