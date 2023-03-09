# Spring框架增强

本组件主要为spring框架提供一些简单的功能增强，包含

* [message-source](message-source): 消息系统的增强，主要内容是增加支持yaml格式的本地化消息文件
* [validation](validation): 验证组件增强，主要是解决之前的验证组件与消息源分裂，无法支持多个错误信息文件的问题。此外，额外补充了一些常见的认证注解，并使用注解的类路径作为报错信息而不需要额外指定
* [infrastructure](infrastructure): 基础组件增强，主要是增强spring的bean，应用上下文等基本组件的能力
* [aop](aop): 增强切面的相关功能