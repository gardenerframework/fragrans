# 引言

接口的操作人是一个在审计领域非常重要的数据，广泛地用于日志记录、数据的创建人、更新人字段。有关如何获取操作人，不同的应用有着不同的实现方法，且在记录的时候有时候因为忘记传参等又造成了遗漏。于是，本组件就着眼这种需求进行开发

# Operator

`Operator` 用于标记当前的操作人

```java
public interface Operator {
    /**
     * 用户id
     */
    String userId = "";
    /**
     * 客户端id
     */
    String clientId = "";
}

```

`DefaultOperator`是由本组件生成的一个scope=request的bean，它主要在请求中提供有关操作人的详细数据，包含

* 操作客户端的id
* 客户端的角色(字符串集合)
* 客户端的权限(字符串集合)
* 操作人的id
* 操作人的角色(字符串集合)
* 操作人的权限(字符串集合)

这些常规数据来协助业务逻辑获取当前的操作人

当任何在请求过程中需要操作人详情的场景下，对比以往的参数传递，都推进直接`@Autowire Operator`来获得操作人的bean

<font color=orange>警告:</font> 需要注意，这个bean只在servlet的线程内生效，无法向其它线程进行传递。如果需要传递，则线程的执行方法应当获取一份当前对象的拷贝

# 与log-generic自动配合

log-generic提供了记录带有操作人的日志的功能，于是为了简化日志记录过程中与本组件的配合，security-operator-auto-logging会使用aop拦截`GenericOperationLogger`
的方法执行并自动注入操作人信息。同时，为了避免无意中修改了程序开发人员的设定值，这样的行为仅在操作人参数为`null`时生效