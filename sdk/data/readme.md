# 引言

data组件主要集合和与数据相关的定义和操作

* [common](common): 负责与数据定义，持久化和操作相关的共用代码定义
* [data-schema](data-schema): 负责标准化的数据定义，主要是与数据持久化相关的数据定义
* [data-cache-core](data-cache-core): 负责进行数据缓存的操作
* [data-persistence](data-persistence): 负责数据持久化的一些常见的orm扫描等定义，主要适配的是数据库操作
* [data-unique-id](data-unique-id): 负责使用雪花算法生成唯一id
