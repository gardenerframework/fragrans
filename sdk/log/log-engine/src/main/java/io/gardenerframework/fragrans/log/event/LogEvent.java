package io.gardenerframework.fragrans.log.event;

import io.gardenerframework.fragrans.log.schema.template.Template;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Date;

/**
 * @author zhanghan30
 * @date 2022/6/8 2:34 下午
 */
@AllArgsConstructor
@Getter
public class LogEvent {
    /**
     * 时间发生的时间
     */
    private final Date timestamp = new Date();
    /**
     * 日志记录类的名称
     */
    private final String loggerName;
    /**
     * 使用的日志模板
     */
    private final Template template;
    /**
     * 所有词汇
     */
    private final Collection<Word> words;
    /**
     * 抛出的异常
     */
    @Nullable
    private final Throwable cause;
}