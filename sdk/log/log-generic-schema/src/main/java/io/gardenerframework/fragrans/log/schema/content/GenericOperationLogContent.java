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
public class GenericOperationLogContent extends AbstractGenericLogContent {
    /**
     * 发生了什么
     */
    private final Word operation;
    /**
     * 最后怎么样了
     */
    private final Word state;

    @Override
    public Collection<Word> getContents() {
        return Arrays.asList(getWhatInWord(), getOperation() == null ? new SimpleWord("") : getOperation(), getState() == null ? new SimpleWord("") : getState(), getDetailInWord());
    }
}
