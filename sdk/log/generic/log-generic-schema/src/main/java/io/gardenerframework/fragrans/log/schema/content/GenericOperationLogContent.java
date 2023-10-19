package io.gardenerframework.fragrans.log.schema.content;

import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.Getter;
import lombok.NonNull;
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
    @NonNull
    private final Word operation;
    /**
     * 最后怎么样了
     */
    @NonNull
    private final Word state;

    @Override
    public Collection<Word> getContent() {
        return Arrays.asList(getWhatInWord(), getOperation(), getState(), getDetailInWord());
    }
}
