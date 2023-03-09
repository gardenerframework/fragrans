package io.gardenerframework.fragrans.event.standard.schema;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2022/9/23 13:48
 */
@Trait
public interface ChangedEvent<O> extends Event {
    O before = null;
    O After = null;
}
