package io.gardenerframework.fragrans.log.schema.content;

import io.gardenerframework.fragrans.log.schema.word.Word;

import java.util.Collection;

public interface Contents {
    /**
     * 返回日志内容
     *
     * @return 内容清单
     */
    Collection<Word> getContents();
}
