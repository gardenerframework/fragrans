package io.gardenerframework.fragrans.log.schema.content;

import io.gardenerframework.fragrans.log.schema.details.OperatorDetail;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

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
    /**
     * 操作方
     */
    @Nullable
    private final OperatorDetail operator;
}
