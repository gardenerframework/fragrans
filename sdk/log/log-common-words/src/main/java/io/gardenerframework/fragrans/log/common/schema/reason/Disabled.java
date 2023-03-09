package io.gardenerframework.fragrans.log.common.schema.reason;

import io.gardenerframework.fragrans.log.schema.word.Word;

/**
 * @author zhanghan30
 * @date 2022/6/17 4:22 下午
 */
public class Disabled implements Word {
    @Override
    public String toString() {
        return "被禁用";
    }
}
