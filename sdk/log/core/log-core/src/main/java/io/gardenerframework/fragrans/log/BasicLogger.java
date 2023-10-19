package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.schema.content.Content;
import io.gardenerframework.fragrans.log.schema.template.Template;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author zhanghan30
 * @date 2022/6/8 2:21 下午
 */
public class BasicLogger {
    private static final Queue<LogMessageCustomizer> messageCustomizers = new ConcurrentLinkedDeque<>();

    public static void addLogMessageCustomizer(@NonNull LogMessageCustomizer customizer) {
        messageCustomizers.add(customizer);
    }

    /**
     * 记录debug
     *
     * @param logger   日志类
     * @param template 模板
     * @param content 内容
     * @param cause    异常
     */
    public void debug(@NonNull Logger logger, @NonNull Template template, @NonNull Content content, @Nullable Throwable cause) {
        logInternally(logger, logger::isDebugEnabled, logger::debug, template, content, cause);
    }

    /**
     * 记录debug
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     * @param cause    异常
     */
    public void debug(@NonNull Logger logger, @NonNull Template template, @NonNull Collection<Word> words, @Nullable Throwable cause) {
        debug(logger, template, new BasicContent(words), cause);
    }

    /**
     * 记录debug
     *
     * @param logger   日志类
     * @param template 模板
     * @param content 内容
     */
    public void debug(@NonNull Logger logger, @NonNull Template template, @NonNull Content content) {
        debug(logger, template, content, null);
    }

    /**
     * 记录debug
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     */
    public void debug(@NonNull Logger logger, @NonNull Template template, @NonNull Collection<Word> words) {
        debug(logger, template, words, null);
    }

    /**
     * 记录info
     *
     * @param logger   日志类
     * @param template 模板
     * @param content 内容
     * @param cause    异常
     */
    public void info(@NonNull Logger logger, @NonNull Template template, @NonNull Content content, @Nullable Throwable cause) {
        logInternally(logger, logger::isInfoEnabled, logger::info, template, content, cause);
    }

    /**
     * 记录 info
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     * @param cause    异常
     */
    public void info(@NonNull Logger logger, @NonNull Template template, @NonNull Collection<Word> words, @Nullable Throwable cause) {
        info(logger, template, new BasicContent(words), cause);
    }

    /**
     * 记录info
     *
     * @param logger   日志类
     * @param template 模板
     * @param content 内容
     */
    public void info(@NonNull Logger logger, @NonNull Template template, @NonNull Content content) {
        info(logger, template, content, null);
    }

    /**
     * 记录 info
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     */
    public void info(@NonNull Logger logger, @NonNull Template template, @NonNull Collection<Word> words) {
        info(logger, template, words, null);
    }

    /**
     * 记录warn
     *
     * @param logger   日志类
     * @param template 模板
     * @param content 内容
     * @param cause    异常
     */
    public void warn(@NonNull Logger logger, @NonNull Template template, @NonNull Content content, @Nullable Throwable cause) {
        logInternally(logger, logger::isWarnEnabled, logger::warn, template, content, cause);
    }

    /**
     * 记录warn
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     * @param cause    异常
     */
    public void warn(@NonNull Logger logger, @NonNull Template template, @NonNull Collection<Word> words, @Nullable Throwable cause) {
        warn(logger, template, new BasicContent(words), cause);
    }


    /**
     * 记录warn
     *
     * @param logger   日志类
     * @param template 模板
     * @param content 内容
     */
    public void warn(@NonNull Logger logger, @NonNull Template template, @NonNull Content content) {
        warn(logger, template, content, null);
    }

    /**
     * 记录warn
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     */
    public void warn(@NonNull Logger logger, @NonNull Template template, @NonNull Collection<Word> words) {
        warn(logger, template, words, null);
    }

    /**
     * 记录error
     *
     * @param logger   日志类
     * @param template 模板
     * @param content 内容
     * @param cause    异常
     */
    public void error(@NonNull Logger logger, @NonNull Template template, @NonNull Content content, @Nullable Throwable cause) {
        logInternally(logger, logger::isErrorEnabled, logger::error, template, content, cause);
    }

    /**
     * 记录 error
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     * @param cause    异常
     */
    public void error(@NonNull Logger logger, @NonNull Template template, @NonNull Collection<Word> words, @Nullable Throwable cause) {
        error(logger, template, new BasicContent(words), cause);
    }

    /**
     * 记录error
     *
     * @param logger   日志类
     * @param template 模板
     * @param content 内容
     */
    public void error(@NonNull Logger logger, @NonNull Template template, @NonNull Content content) {
        error(logger, template, content, null);
    }

    /**
     * 记录 error
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     */
    public void error(@NonNull Logger logger, @NonNull Template template, @NonNull Collection<Word> words) {
        error(logger, template, words, null);
    }

    /**
     * 真正记录日志的方法
     *
     * @param logger          日志记录
     * @param logLevelChecker 检查日志记录是否激活
     * @param methodTemplate  真正的日志记录方法
     * @param template        模板
     * @param content        内容
     * @param cause           异常
     */
    protected void logInternally(@NonNull Logger logger, @NonNull LogLevelChecker logLevelChecker, @NonNull LogMethodTemplate methodTemplate, @NonNull Template template, @NonNull Content content, @Nullable Throwable cause) {
        for (LogMessageCustomizer messageCustomizer : messageCustomizers) {
            if (messageCustomizer.support(this, template, content)) {
                //完成客制化处理
                template = messageCustomizer.customize(template);
                content = messageCustomizer.customize(content);
                //继续执行，更改完成后的也需要检查后面是否还有继续的更改
            }
        }
        if (logLevelChecker.isEnabled()) {
            Collection<Object> contents = new ArrayList<>(content.getContent().size() + (cause == null ? 0 : 1));
            contents.addAll(content.getContent());
            if (cause != null) {
                contents.add(cause);
            }
            methodTemplate.log(template.toString(), contents.toArray(new Object[]{}));
        }
    }


    /**
     * 一个slf4j的日志登记是否启用的方法签名
     */
    @FunctionalInterface
    private interface LogLevelChecker {
        /**
         * 当前登记是否激活
         *
         * @return 是否激活
         */
        boolean isEnabled();
    }

    /**
     * 一个slf4j的日志记录的方法签名
     */
    @FunctionalInterface
    private interface LogMethodTemplate {
        /**
         * 执行的日志方法
         *
         * @param format    格式
         * @param arguments 参数
         */
        void log(@NonNull String format, Object... arguments);
    }

    @AllArgsConstructor
    @Getter
    private static class BasicContent implements Content {
        @NonNull
        private final Collection<Word> content;
    }
}
