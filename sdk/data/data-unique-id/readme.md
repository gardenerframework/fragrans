# 引言

本工程实现一个基于雪花算法的分布式id生成器

# HostIdGenerator

雪花算法的一部分是以主机id作为识别符号的，这个接口就是用来生成这部分识别符号

```java
public interface HostIdGenerator {
    /**
     * 生成id
     *
     * @return id
     */
    String getHostId();
}
```

# IpAddressHoseIdGenerator

使用ip地址作为主机id生成器

# UniqueIdGenerator

实际的唯一id生成器，要求注入一个`HostIdGenerator`

# @BusinessCode

生成唯一id的时候，可以附加一个字符作为业务前缀。这个注解可以给实体类上添加这个业务字符