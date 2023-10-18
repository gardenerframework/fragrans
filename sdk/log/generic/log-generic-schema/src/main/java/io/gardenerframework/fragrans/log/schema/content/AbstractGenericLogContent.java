package io.gardenerframework.fragrans.log.schema.content;

import io.gardenerframework.fragrans.log.schema.details.Detail;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

/**
 * @author ZhangHan
 * @date 2022/6/9 0:57
 */
@SuperBuilder
@Getter
public abstract class AbstractGenericLogContent implements Contents {
    /**
     * 什么东西
     */
    @NonNull
    private final Class<?> what;
    /**
     * 详情
     */
    @Nullable
    private final Detail detail;

    protected Word getWhatInWord() {
        return new Word() {
            @Override
            public String toString() {

                return what.getSimpleName();
            }
        };
    }

    protected Word getDetailInWord() {
        return new Word() {
            @Override
            public String toString() {
                return detail == null ? "" : String.format("[%s]", String.join(", ", detail.getPairs()));
            }
        };
    }
}
