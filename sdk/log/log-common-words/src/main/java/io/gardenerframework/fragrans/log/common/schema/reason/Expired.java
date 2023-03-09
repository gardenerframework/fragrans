package io.gardenerframework.fragrans.log.common.schema.reason;

import io.gardenerframework.fragrans.log.schema.word.Word;

/**
 * @author zhanghan30
 * @date 2022/6/9 3:16 下午
 */
public class Expired implements Word {
    @Override
    public String toString() {
        return "已过期";
    }
}
