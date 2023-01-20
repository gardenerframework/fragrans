package io.gardenerframework.fragrans.api.security.operator.schema;

import io.gardenerframework.fragrans.api.security.schema.trait.ApiSecurityTraits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
public class OperatorBrief implements
        ApiSecurityTraits.OperatorTraits.User,
        ApiSecurityTraits.OperatorTraits.Client {
    /**
     * 用户id
     */
    @Nullable
    private String userId;
    /**
     * 客户端id
     */
    @Nullable
    private String clientId;
}
