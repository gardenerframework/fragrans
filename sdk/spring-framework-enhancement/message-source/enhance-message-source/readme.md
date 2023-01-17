# 引言

`MessageSource`负责spring框架中的大部分国际化工作，但是它使用起来有些不顺手的地方

* 只能读properties文件，别的格式或者数据源不支持
* 在application.yaml中配置国际化文件，这个配置项编写中间组件时无法预置

而从长久以来的实践看，需要的是

* 中间组件包需要自己指定国际化文件，这些文件的名称不应当在运行时的application.yaml中的配置项被覆盖
* 支持多种格式，比如yaml或者甚至是缓存或是数据库或者远程对象存储等

本组件就主要聚焦于这些实际的需求

# 预置消息资源清单

第一个问题是解决消息的资源清单如何预置。在此需要实现`BasenameProvider`

```java
public interface BasenameProvider {
    /**
     * 给出basename
     *
     * @return basename
     * @throws Exception 出现问题
     */
    Set<String> getBasenames() throws Exception;
}
```

这个接口返回需要加入到消息源中的所有资源的basename。

当前组件提供了一个在"MESSAGE-INF"目录下寻找"basenames.yaml"文件的实现，在这个yaml文件中，用以下格式来表达所有需要的basename

```yaml
com:
  jdlcoud:
    gardener:
      fragrans:
        messages:
          basenames:
            - file1
            - file2
```

这样，本组件实现的消息源就会去加载给出的所有文件

<font color=orange>注意</font>: basename是消息源认定的资源的基础名称，这个名称在实际加载时还会和当前的语言环境以及下文即将讲解的格式配合在一起，所以不要认为消息源总是只加载文件

# 添加资源格式

第二个问题解决只能读取properties文件的问题。在此，首先开发人员需要确定资源的格式。 格式并不只是文件的格式，比如yaml、xml，而是代表了资源如何读取和解析，例如"database"格式的资源表达需要从数据库中进行读取。
因此可以简单地将格式理解为资源的类型，开发人员觉得一个类型的名称，并编写一个`ResourceBundleLoader`来进行读取。

```java
public interface ResourceBundleLoader {
    /**
     * 加载资源
     *
     * @param baseName   资源名
     * @param bundleName 推荐的资源包名称
     * @param locale     本地信息
     * @param charset    字符集
     * @param loader     类加载器
     * @param reload     是否是重新加载(意味着不应当从缓存中读取)
     * @return 加载后的资源，如果么有返回null
     * @throws Exception 加载过程中遇到的问题
     */
    @Nullable
    ResourceBundle load(String baseName, String bundleName, Locale locale, String charset, ClassLoader loader, boolean reload) throws Exception;
}
```

* 第一步: 确定格式的名称
* 第二步: 编写`ResourceBundleLoader`并加上`@ResourceFormat`注解

这样，当本组件认为需要加载资源时，就会调用loader去实现资源的加载和解析

目前组件实现了`PropertyFileResourceBundleLoader`和`YamlFileResourceBundleLoader`
，一个加载properties文件，一个加载yaml文件。所以配合basename来说，`BasenameProvider`给出的资源名称或者有一个properties文件或者有一个yaml文件，两者有一个就行

# 简化消息源使用

在`MessageSource`
使用过程中，核心的事情是确定消息的编码以及消息模板中需要填充的参数。为此开发人员可能需要编写一个枚举或者常量类来维护一个复杂的注册表。对此，长期以来的最佳实现认为当需要进行消息的格式化时，直接将比如异常等的类名作为编码来使用可大量节省开发时间

```java
public interface EnhancedMessageSource extends MessageSource {
    /**
     * 返回基于类名称获取的消息
     *
     * @param target         目标
     * @param defaultMessage 默认消息
     * @param locale         本地信息
     * @param <T>            没啥用
     * @return 消息
     */
    default <T> String getMessage(T target, @Nullable String defaultMessage, Locale locale) {
        return getMessage(
                target instanceof String ? (String) target :
                        target.getClass().getCanonicalName(), target instanceof MessageArgumentsSupplier ? ((MessageArgumentsSupplier) target).getMessageArguments() : null,
                defaultMessage,
                locale
        );
    }

    /**
     * 返回基于类名称获取的消息
     *
     * @param target 目标
     * @param locale 本地信息
     * @param <T>    没啥用
     * @return 消息
     */
    default <T> String getMessage(T target, Locale locale) {
        return getMessage(
                target,
                null,
                locale
        );
    }

    /**
     * 基于类型获得消息
     *
     * @param target         目标没醒
     * @param defaultMessage 默认消息
     * @param locale         本地信息
     * @return 消息
     */
    default String getMessage(Class<?> target, @Nullable String defaultMessage, Locale locale) {
        return getMessage(
                target.getCanonicalName(),
                null,
                defaultMessage,
                Locale.getDefault()
        );
    }

    /**
     * 基于类型获得消息
     *
     * @param target 目标没醒
     * @param locale 本地信息
     * @return 消息
     */
    default String getMessage(Class<?> target, Locale locale) {
        return getMessage(target, null, locale);
    }
}
```

对此，本组件对原来的消息源接口进行了增强。说白了就是可以用一个类名或者一个对象作为"编码"使用，并且如果这个对象实现了`MessageArgumentsSupplier`，在格式化消息的时候会直接调用接口给出消息模板所需的所有参数

```java
public interface MessageArgumentsSupplier {
    /**
     * 获得消息参数数组
     *
     * @return 消息参数数组
     * @see MessageSource#getMessage(String, Object[], Locale)
     */
    Object[] getMessageArguments();
}
```