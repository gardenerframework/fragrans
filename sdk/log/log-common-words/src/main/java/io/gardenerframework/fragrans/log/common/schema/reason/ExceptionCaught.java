package io.gardenerframework.fragrans.log.common.schema.reason;

import io.gardenerframework.fragrans.log.schema.word.Word;

/**
 * @author zhanghan30
 * @date 2022/6/9 3:16 下午
 */
public class ExceptionCaught implements Word {
    @Override
    public String toString() {
        return "捕捉到异常";
    }
}
