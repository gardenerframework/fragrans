# 引言

安全包含3个部分: 认证(Authentication)、授权(Authorization)、审计(Auditing)。当前组件就着重这三点进行开发

# 标准化数据

## User & Client

```java

@Trait
interface User {
    String userId = "";
}

@Trait
interface Client {
    /**
     * 客户端id
     */
    String clientId = "";
}
```

用户或客户端(应用程序)是认证、授权、审计的基本要素，其主要就是包含了用户的id和应用程序(客户端)的id，这些id能够对接存储库或接口转换为详细的信息

## ClientAuthorization & UserAuthorization

```java

@Trait
public interface ClientAuthorization {
    /**
     * 客户端角色
     */
    @Nullable
    Collection<String> clientRoles = null;
    /**
     * 客户端权限
     */
    @Nullable
    Collection<String> clientPrivileges = null;
}

public interface UserAuthorization {
    /**
     * 用户角色
     */
    Collection<String> userRoles = null;
    /**
     * 用户权限
     */
    Collection<String> userPrivileges = null;
}
```

包含了客户与用户的角色以及授权操作的权限，它是一个标准的数据格式定义。用例包含

* 查询用户的全局权限

```java
public class User implements UserAuthorization {
    private String id;
    //其它属性
    private Collection<String> userRoles = null;
    private Collection<String> userPrivileges = null;
}
```

* 基于组织、店铺、群组等查询用户在范围内的权限

```java
public class User implements UserAuthorizationForGroup {
    /**
     * 权限对应的组织
     */
    private String groupId;
    //其它属性
    private Collection<String> userRoles = null;
    private Collection<String> userPrivileges = null;
}
```

## OperationAuditLog

顾名思义，`OperationAuditLog`代表的是审计记录

```java
public class OperationAuditLog {
    /**
     * 时间戳
     */
    @Builder.Default
    private Date timestamp = new Date();
    /**
     * 操作用户
     */
    @Nullable
    private String userId;
    /**
     * 操作客户端
     */
    @Nullable
    private String clientId;
    /**
     * 操作设备
     */
    @Nullable
    private String deviceId;
    /**
     * ip地址
     */
    @NonNull
    private String ip;

    /**
     * http方法
     */
    @NonNull
    private String method;

    /**
     * uri
     */
    @NonNull
    private String uri;

    /**
     * queryString
     */
    @Nullable
    private String queryString;
    /**
     * http头
     */
    @Singular
    @NonNull
    private Map<String, Collection<String>> headers;
    /**
     * 请求体
     */
    @Nullable
    private String requestBody;
    /**
     * http状态码
     */
    @Builder.Default
    private int status = 0;
    /**
     * 响应体
     */
    @Nullable
    private String responseBody;
    /**
     * 业务编码
     */
    @Nullable
    private String code;
    /**
     * 是否成功的标记
     */
    @Builder.Default
    private boolean success = true;
    /**
     * 变更前快照
     */
    @Nullable
    private String snapshot;
    /**
     * 其它上下文
     */
    @Nullable
    private String context;
}
```

审计的发生时间点是在任何对外暴露的接口被调用时，接口可能是被`@Controller`封装的方法，也可能是某个Filter的处理方法。

# Operator感知

[api-security-operator-aware](api-security-operator-aware)定义了接口操作人感知的能力

```java
public class OperatorBrief implements
        ApiSecurityTraits.OperatorTraits.User,
        ApiSecurityTraits.OperatorTraits.Client {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 客户端id
     */
    private String clientId;
}
```

这是一个"scope=request"的bean，主要是给controller方法或者其它请求处理期间的类进行`@Autowire`
后获取当前的操作人。需要注意的是这个组件需要配合其它安全组件使用才有意义。因为这个对象需要从安全组件中读取出来用户后进行初始化。 比如如果搭配spring security，则需要从`Authentication`
中读取出来当前访问的用户以及客户端

# 安全自动化

## 日志自动化

当加载了[api-security-automation-logging](api-security-automation-logging)
和[api-security-operator-aware](api-security-operator-aware)组件后，它会在`GenericOperationLogger`写日志的时候附加上操作人信息，变成这样

```log
用户信息更新成功[userid=123, name=张三], 操作人: [userId=456, clientId=abc]
```

## 操作人员数据持久自动化

"data-schema"组件中定义了保存创建人，更新人以及相关时间的数据属性，这些属性需要开发人员在操作数据库的过程中自行编写代码，通过获取用户信息，客户端信息等完成。 在"Operator感知"
模块的帮助下，可以通过aop拦截等方法，自动向dao的操作方法中注入操作人以及相关的时间数据

### 要求dao的参数对象实现的trait

要使用这一特性，首先dao mapper的操作对象需要实现`Creator`、`Updater`的trait



# 采集器与存储器的协同关系

原则上一个服务的审计日志会利用单独的消息队列管道(如kafka的topic)
发送给存储器，存储器基于管道来源决定日志所属的接口和应用并进行一些额外的逻辑操作(比如附加一些用户的姓名等业务相关的数据)
后存储到对应的库表或es索引内。这种协同关系较为合理，理由是不同的服务由不同的团队开发，每一个团队最终落库的审计记录都不相同，没有必要由一个存储器来负责所有服务的审计记录落地工作