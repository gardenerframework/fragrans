package com.jdcloud.gardener.fragrans.api.security.schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2021/12/1 3:53 下午
 */
@Getter
@Setter
@NoArgsConstructor
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@ConditionalOnMissingBean(value = Operator.class, ignored = DefaultOperator.class)
public class DefaultOperator implements Operator {
    /**
     * 客户端id
     */
    private String clientId;
    /**
     * 客户端角色
     */
    @Nullable
    private Collection<String> clientRoles;
    /**
     * 客户端权限
     */
    @Nullable
    private Collection<String> clientPrivileges;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户角色
     */
    private Collection<String> userRoles;
    /**
     * 用户权限
     */
    private Collection<String> userPrivileges;
}
