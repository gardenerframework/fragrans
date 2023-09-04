package io.gardenerframework.fragrans.data.schema.entity;

import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;

/**
 * @author zhanghan30
 * @date 2023/9/4 16:50
 */
public interface BasicOperationTraceableEntitySkeleton<T> extends
        BasicEntitySkeleton<T>,
        SecurityTraits.AuditingTraits.IdentifierTraits.Creator,
        SecurityTraits.AuditingTraits.IdentifierTraits.Updater {
}
