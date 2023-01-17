package com.jdcloud.gardener.fragrans.api.idempotent.log.target;

import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import com.jdcloud.gardener.fragrans.log.schema.reason.subject.Subject;

/**
 * @author zhanghan30
 * @date 2022/2/24 2:57 下午
 */
@LogTarget("幂等因子")
public class IdempotentFactorTarget implements Subject {
    @Override
    public String getSubject() {
        return "幂等因子";
    }
}
