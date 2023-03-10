package io.gardenerframework.fragrans.log.schema.content;

import io.gardenerframework.fragrans.log.schema.word.SimpleWord;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/6/9 1:00
 */
@SuperBuilder
@Getter
public class GenericBasicLogContent extends AbstractGenericLogContent {
    /**
     * 发生了什么
     */
    private final Word how;

    @Override
    public Collection<Word> getContents() {
        return Arrays.asList(getWhatInWord(), getHow() == null ? new SimpleWord("") : getHow(), getDetailInWord());
    }
}
