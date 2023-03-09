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