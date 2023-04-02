# 接口选项托管

大部分api接口都提供需要持久化保存的配置项，注意这里的配置项和配置文件不一样。比如

```yaml
server:
  port: 80
```

这是一个配置项，但是更应该放在配置文件中，而其他的业务相关的选项则更适合保存在数据库等位置

## 选项的注册

`@ApiOption`注解用于定义一个api选项，被定义的选项需要将自己声明为bean，选项的id就是bean
name。此外，注解提供readonly属性，表明选项是一个只读的数据。 只读选项是真实存在的，比如当前服务器的加密公钥。
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

选项的持久化由`ApiOptionPersistenceService`来完成，目前给出的是基于mysql数据库的版本"api-options-persistence-database"
，它会要求固定读写"api-option"表