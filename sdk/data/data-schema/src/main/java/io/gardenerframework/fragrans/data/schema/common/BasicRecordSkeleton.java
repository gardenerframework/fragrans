package io.gardenerframework.fragrans.data.schema.common;

import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;

/**
 * @author zhanghan30
 * @date 2023/9/4 15:55
 */
public interface BasicRecordSkeleton extends
        SecurityTraits.AuditingTraits.DatetimeTraits.CreatedTime,
        SecurityTraits.AuditingTraits.DatetimeTraits.LastUpdateTime {
}
