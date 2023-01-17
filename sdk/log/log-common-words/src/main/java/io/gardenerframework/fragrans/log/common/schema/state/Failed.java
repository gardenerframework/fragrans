package io.gardenerframework.fragrans.log.common.schema.state;

import io.gardenerframework.fragrans.log.schema.word.Word;

/**
 * @author zhanghan30
 * @date 2022/6/9 3:16 下午
 */
public class Failed implements Word {
    @Override
    public String toString() {
        return "失败";
    }
}
