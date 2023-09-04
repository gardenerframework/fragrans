package io.gardenerframework.fragrans.data.schema.common;

import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;

/**
 * @author zhanghan30
 * @date 2023/9/4 16:07
 */
public interface OperationTraceable extends
        SecurityTraits.AuditingTraits.IdentifierTraits.Creator,
        SecurityTraits.AuditingTraits.IdentifierTraits.Updater {
}
