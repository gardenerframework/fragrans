package io.gardenerframework.fragrans.data.schema.relation;

import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;

/**
 * @author zhanghan30
 * @date 2023/9/4 16:51
 */
public interface BasicOperationTraceableRelationSkeleton extends
        BasicRelationSkeleton,
        SecurityTraits.AuditingTraits.IdentifierTraits.Creator,
        SecurityTraits.AuditingTraits.IdentifierTraits.Updater {
}
