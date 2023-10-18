package io.gardenerframework.fragrans.log.schema.word;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * @author zhanghan30
 * @date 2023/1/16 20:28
 */
@AllArgsConstructor
public class SimpleWord implements Word {
    @NonNull
    private final String word;

    @Override
    public String toString() {
        return word;
    }
}
