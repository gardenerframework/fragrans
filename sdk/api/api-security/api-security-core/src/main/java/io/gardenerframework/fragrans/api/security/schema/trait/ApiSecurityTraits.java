package io.gardenerframework.fragrans.api.security.schema.trait;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;
import org.springframework.lang.Nullable;

import java.util.Collection;

public interface ApiSecurityTraits {
    interface OperatorTraits {
        @Trait
        interface User {
            String userId = "";
        }

        @Trait
        interface Client {
            /**
             * 客户端id
             */
            String clientId = "";
        }
    }

    interface AuthorizationTraits {
        @Trait
        interface ClientAuthorization {
            /**
             * 客户端角色
             */
            @Nullable
            Collection<String> clientRoles = null;
            /**
             * 客户端权限
             */
            @Nullable
            Collection<String> clientPrivileges = null;
        }

        @Trait
        interface UserAuthorization {
            /**
             * 用户角色
             */
            Collection<String> userRoles = null;
            /**
             * 用户权限
             */
            Collection<String> userPrivileges = null;
        }
    }

    interface AuditTraits {
        @Trait
        interface DeviceId {
            /**
             * 设备id
             */
            String deviceId = "";
        }

        @Trait
        interface Snapshot {
            /**
             * 操作快照
             */
            String snapshot = "";
        }

        @Trait
        interface Context {
            /**
             * 上下文信息
             */
            String context = "";
        }
    }

}
