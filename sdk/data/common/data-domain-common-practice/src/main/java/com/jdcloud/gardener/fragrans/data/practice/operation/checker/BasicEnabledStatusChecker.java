package com.jdcloud.gardener.fragrans.data.practice.operation.checker;

import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.log.common.schema.reason.Disabled;
import com.jdcloud.gardener.fragrans.log.schema.word.Word;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2022/6/17 4:07 下午
 */
@SuperBuilder
public abstract class BasicEnabledStatusChecker<I, R extends GenericTraits.StatusTraits.EnableFlag> extends BasicChecker<I, R> {
    @Override
    protected Word getLogHow() {
        return new Disabled();
    }

    @Override
    protected boolean doCheck(@Nullable R record) {
        return Objects.requireNonNull(record).isEnabled();
    }
}
