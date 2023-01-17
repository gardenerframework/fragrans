package com.jdcloud.gardener.fragrans.data.persistence.template.sql;

/**
 * 表达实现类是个sql api接口
 * <p>
 * 该接口用于生成从外部可见的sql语句
 * <p>
 * 比如用户和角色关联查询时，用户需要角色开发生成一些关联查询语句，则这部分语句就应当写在RoleSqlApi中
 *
 * @author zhanghan30
 * @date 2022/11/3 16:21
 */
public interface DomainSqlApi {
    /**
     * 基于mapper的类型获取mapper对应的实体类型
     *
     * @param mapperType mapper类型
     * @return 实体类型
     */
    Class<?> getDomainObjectType(Class<?> mapperType);
    //其余的方法推荐是生成sql语句的方法
}
