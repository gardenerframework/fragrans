# 项目目标

fragrans(桂花)，为gardenerframework的其它开源项目提供基本的、公共的编程框架支持。它将不断将各个项目中经常被使用到的通用的、非业务相关的组件以及标准化和规范化的组件纳入进来维护和沉淀

# 功能清单

* [sugar](sdk/sugar): 这个组件主要用来沉淀一些加快开发过程的语法糖注解。比如`@Trait`注解用于标记一个类或者一个接口是一组属性特性。
* [log](sdk/log): 该组件主要提供全局的标准化日志消息格式以及日志事件
* [design-pattern](sdk/design-pattern): 设计模式相关的组件
* [spring-framework-enhancement](sdk/spring-framework-enhancement): 该组件沉淀一些对spring框架的增强功能
* [data](sdk/data): 该组件负责标准化数据结构的定义、常见的缓存和数据库的存取以及轻量级的orm框架，并负责沉淀常见的属性特性
* event-driven: 负责应用之间基于事件的rpc通信
* api: 负责http api接口编写相关的组件，包含标准错误处理，http请求与响应的数据格式，api切面以及选项托管和安全
* audit: 负责操作审计相关的框架设计