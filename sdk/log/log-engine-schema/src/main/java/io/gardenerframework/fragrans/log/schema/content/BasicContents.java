package io.gardenerframework.fragrans.log.schema.content;

import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Singular;

import java.util.Collection;
import java.util.List;

/**
 * 基本内容
 */
@Getter
@AllArgsConstructor
public class BasicContents implements Contents {
    @Singular
    private final Collection<Word> contents;
}
