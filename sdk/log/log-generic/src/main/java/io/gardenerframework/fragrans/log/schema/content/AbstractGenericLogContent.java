package io.gardenerframework.fragrans.log.schema.content;

import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

/**
 * @author ZhangHan
 * @date 2022/6/9 0:57
 */
@SuperBuilder
@Getter
public abstract class AbstractGenericLogContent {
    /**
     * 什么东西
     */
    private final Class<?> what;
    /**
     * 详情
     */
    @Nullable
    private final Detail detail;
}
