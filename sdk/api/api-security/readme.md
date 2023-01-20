# 引言

安全包含3个部分: 认证(Authentication)、授权(Authorization)、审计(Auditing)。当前组件就着重这三点进行开发

# User & Client

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

操作方(人或应用程序)是认证、授权、审计的基本要素，其主要就是包含了用户的id和应用程序(客户端)的id，这些id能够对接存储库或接口转换为详细的信息

# ClientAuthorization & UserAuthorization

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

# OperationAuditRecord

审计的目标是为了说清楚发生了什么事情，包含

| 属性      | 类型      | 是否必须 | 含义                       |
|---------|---------|------|--------------------------|
| 时间      | date    | 1    | 什么时间进行的操作                |
| 操作人     | string  | 0    | 由谁进行的操作，通常是用户的id或用户名     |
| 使用的设备   | string  | 0    | 使用的设备指纹                  |
| 使用的应用   | string  | 0    | 使用的应用程序名称或id             |
| 所在的网络位置 | string  | 1    | ip地址                     |
| 所在的地理位置 | json    | 0    | 国家和地区                    |
| 操作方法    | string  | 1    | http方法                   |
| 被操作的目标  | string  | 1    | url没有查询字符串               |
| 查询字符串   | string  | 0    | 查询字符串                    |
| http头   | json    | 1    | http头                    |
| 操作参数详情  | string  | 1    | 操作的入参(http Body)         |
| 操作响应状态  | int     | 1    | http状态码                  |
| 操作业务状态码 | String  | 0    | 操作响应自定义的状态或错误编码          |
| 操作响应详情  | string  | 1    | 操作的响应                    |
| 操作结果    | boolean | 1    | 是否成功的判断                  |
| 操作前数据快照 | json    | 0    | 如果是修改，删除等操作，操作之前的数据快照是什么 |
| 其他上下文信息 | json    | 0    | 其它扩展信息                   |

* 设备指纹用来在审计中表达用户使用的硬件设备是否发生变化
* 应用是访问端的应用id
* ip地址一般需要网管层传入，现在的系统一般都是远程调用，因此ip地址一般会存在
* 国家和地理位置则需要对接ip地址信息库
* 被操作的目标往往是一个接口地址或rpc的一个方法名称(不一定是类的某个方法)
* 操作动作则需要一定程度的分析，比如
    * restful的接口使用http方法来表达动作，比如"get /user"和"post /user"，操作目标都是用户，但行为不同
    * 非http服务则一般需要从参数或者方法名上进行定义了
* 操作结果，其实一般就是成功与否的标记，如果是http接口，则4xx和5xx和接口方法抛出异常一概代表失败，其它情况则需要额外分析响应结果是否符合预期

# 审计信息收集器 & 存储器

审计信息收集器用来快速收集审计信息后立刻发送给独立进程的存储器，它不做任何关联查询(比如拿ip查地址库之类的)
。采集器向消息队列存储数据后由存储器消费消息后再执行低速操作，比如查询用户的具体信息之类的

# RootOperationAuditRecord