package io.gardenerframework.fragrans.log.common.schema.reason;

import io.gardenerframework.fragrans.log.schema.word.Word;

/**
 * @author zhanghan30
 * @date 2022/7/8 5:43 下午
 */
public class Mismatch implements Word {
    @Override
    public String toString() {
        return "不匹配";
    }
}
