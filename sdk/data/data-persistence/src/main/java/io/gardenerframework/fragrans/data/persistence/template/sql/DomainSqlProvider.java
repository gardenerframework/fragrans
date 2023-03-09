package io.gardenerframework.fragrans.data.persistence.template.sql;

import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * 定位是向当前的领域mapper提供sql
 * <p>
 * 和{@link DomainSqlApi}的区别是，provider是领域对内，api是领域对外
 *
 * @author zhanghan30
 * @date 2022/11/3 16:28
 */
public interface DomainSqlProvider {
    /**
     * 从ProviderContext中解析当前触发sql provider的mapper在给定模板和实体模板下的实体模板实现
     *
     * @param context 要求提供信息的上下文
     * @return 数据类型
     */
    Class<?> getDomainObjectType(ProviderContext context);
}
