package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.schema.template.Template;
import io.gardenerframework.fragrans.log.schema.word.Word;
import org.slf4j.Logger;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * 主要给子类用的
 *
 * @author ZhangHan
 * @date 2022/6/9 1:31
 */
@FunctionalInterface
public interface BasicLoggerMethodTemplate {
    /**
     * 记录日志的模板方法
     *
     * @param logger   日志记录类
     * @param template 模板
     * @param words    词汇
     * @param cause    异常
     */
    void log(Logger logger, Template template, Collection<Word> words, @Nullable Throwable cause);
}
