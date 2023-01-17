package com.jdcloud.gardener.fragrans.data.persistence.template.sql;

import com.jdcloud.gardener.fragrans.data.persistence.template.support.DomainObjectTemplateTypesResolver;
import lombok.AllArgsConstructor;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 这个类主要的作用是给sql语句类来使用的
 * <p>
 * 更多的模板化的dao的sql语句类的一些共性功能将在这个类上沉淀
 *
 * @author ZhangHan
 * @date 2022/11/2 16:13
 */
@AllArgsConstructor
public abstract class DomainSqlTemplateBase implements DomainSqlProvider, DomainSqlApi {
    private final Class<?> daoTemplate;
    private final Class<?> objectTemplate;

    /**
     * 从ProviderContext中解析当前触发sql provider的mapper在给定模板和实体模板下的实体模板实现
     *
     * @param context 要求提供信息的上下文
     * @return 数据类型
     */
    @Override
    public Class<?> getDomainObjectType(ProviderContext context) {
        //使用context中标记的mapper类型去获取这个mapper实现的业务对象类型
        return getDomainObjectType(context.getMapperType());
    }

    /**
     * 从ProviderContext中解析当前触发sql provider的mapper在给定模板和实体模板下的实体模板实现
     *
     * @param mapperType mapper 类型
     * @return 数据类型
     */
    @Override
    public Class<?> getDomainObjectType(Class<?> mapperType) {
        Assert.isTrue(this.daoTemplate.isAssignableFrom(mapperType), mapperType + " must be a subclass of " + daoTemplate);
        return Objects.requireNonNull(DomainObjectTemplateTypesResolver.resolveTemplateImplementationTypeMappings(
                mapperType,
                daoTemplate
        )).get(objectTemplate);
    }
}
