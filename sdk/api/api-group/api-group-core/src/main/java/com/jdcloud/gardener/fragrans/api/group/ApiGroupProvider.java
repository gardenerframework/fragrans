package com.jdcloud.gardener.fragrans.api.group;

import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * 启动时提供api分组信息
 *
 * @author ZhangHan
 * @date 2022/5/10 21:44
 */
@FunctionalInterface
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
    @Nullable
    default Collection<Class<?>> getAdditionalMembers() {
        return null;
    }
}
