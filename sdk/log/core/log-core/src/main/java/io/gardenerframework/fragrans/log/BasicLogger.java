package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.event.schema.LogEvent;
import io.gardenerframework.fragrans.log.schema.content.Contents;
import io.gardenerframework.fragrans.log.schema.template.Template;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author zhanghan30
 * @date 2022/6/8 2:21 下午
 */
public class BasicLogger implements ApplicationEventPublisherAware {
    /**
     * 引入线程安全的集合
     */
    private final static Set<String> logEventSenderNames = ConcurrentHashMap.newKeySet();
    /**
     * 引入现场安全的队列
     */
    private static final Queue<LogEvent> unsentEvents = new ConcurrentLinkedDeque<>();
    private static final Queue<LogMessageCustomizer> messageCustomizers = new ConcurrentLinkedDeque<>();
    /**
     * 通过共享事件发布器，使得多个实例共享同一个事件发布器
     */
    private static ApplicationEventPublisher eventPublisher;

    public static void addLogMessageCustomizer(@NonNull LogMessageCustomizer customizer) {
        messageCustomizers.add(customizer);
    }

    /**
     * 启用日志事件
     *
     * @param logger  日志记录器
     * @param enabled 是否启用
     */
    public static void enableLogEvent(Logger logger, boolean enabled) {
        if (enabled) {
            logEventSenderNames.add(logger.getName());
        } else {
            logEventSenderNames.remove(logger.getName());
        }
    }

    /**
     * 记录debug
     *
     * @param logger   日志类
     * @param template 模板
     * @param contents 内容
     * @param cause    异常
     */
    public void debug(Logger logger, Template template, Contents contents, @Nullable Throwable cause) {
        logInternally(logger, logger::isDebugEnabled, logger::debug, template, contents, cause);
    }

    /**
     * 记录debug
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     * @param cause    异常
     */
    public void debug(Logger logger, Template template, Collection<Word> words, @Nullable Throwable cause) {
        debug(logger, template, new BasicContents(words), cause);
    }

    /**
     * 记录info
     *
     * @param logger   日志类
     * @param template 模板
     * @param contents 内容
     * @param cause    异常
     */
    public void info(Logger logger, Template template, Contents contents, @Nullable Throwable cause) {
        logInternally(logger, logger::isInfoEnabled, logger::info, template, contents, cause);
    }

    /**
     * 记录 info
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     * @param cause    异常
     */
    public void info(Logger logger, Template template, Collection<Word> words, @Nullable Throwable cause) {
        info(logger, template, new BasicContents(words), cause);
    }

    /**
     * 记录info
     *
     * @param logger   日志类
     * @param template 模板
     * @param contents 内容
     * @param cause    异常
     */
    public void warn(Logger logger, Template template, Contents contents, @Nullable Throwable cause) {
        logInternally(logger, logger::isWarnEnabled, logger::warn, template, contents, cause);
    }

    /**
     * 记录warn
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     * @param cause    异常
     */
    public void warn(Logger logger, Template template, Collection<Word> words, @Nullable Throwable cause) {
        warn(logger, template, new BasicContents(words), cause);
    }

    /**
     * 记录info
     *
     * @param logger   日志类
     * @param template 模板
     * @param contents 内容
     * @param cause    异常
     */
    public void error(Logger logger, Template template, Contents contents, @Nullable Throwable cause) {
        logInternally(logger, logger::isErrorEnabled, logger::error, template, contents, cause);
    }

    /**
     * 记录 error
     *
     * @param logger   日志类
     * @param template 模板
     * @param words    词汇
     * @param cause    异常
     */
    public void error(Logger logger, Template template, Collection<Word> words, @Nullable Throwable cause) {
        error(logger, template, new BasicContents(words), cause);
    }

    @Override
    public synchronized void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        if (eventPublisher == null) {
            //通过同步关键字使得变量只设置一次
            eventPublisher = applicationEventPublisher;
            //当完成初始化的一瞬间把积压的事件发出去
            LogEvent event;
            while ((event = unsentEvents.poll()) != null) {
                eventPublisher.publishEvent(event);
            }
        }
    }


    /**
     * 真正记录日志的方法
     *
     * @param logger          日志记录
     * @param logLevelChecker 检查日志记录是否激活
     * @param methodTemplate  真正的日志记录方法
     * @param template        模板
     * @param contents        内容
     * @param cause           异常
     */
    private void logInternally(Logger logger, LogLevelChecker logLevelChecker, LogMethodTemplate methodTemplate, Template template, Contents contents, @Nullable Throwable cause) {
        for (LogMessageCustomizer messageCustomizer : messageCustomizers) {
            if (messageCustomizer.support(this, template, contents)) {
                //完成客制化处理
                template = messageCustomizer.customize(template);
                contents = messageCustomizer.customize(contents);
                //继续执行，更改完成后的也需要检查后面是否还有继续的更改
            }
        }
        if (logLevelChecker.isEnabled()) {
            Collection<Object> content = new ArrayList<>(contents.getContents().size() + (cause == null ? 0 : 1));
            content.addAll(contents.getContents());
            if (cause != null) {
                content.add(cause);
            }
            methodTemplate.log(template.toString(), content.toArray(new Object[]{}));
        }
        if (logEventSenderNames.contains(logger.getName())) {
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new LogEvent(logger.getName(), template, contents, cause));
                LogEvent event;
                while ((event = unsentEvents.poll()) != null) {
                    eventPublisher.publishEvent(event);
                }
            } else {
                unsentEvents.add(new LogEvent(logger.getName(), template, contents, cause));
            }
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
        void log(String format, Object... arguments);
    }

    @AllArgsConstructor
    @Getter
    private static class BasicContents implements Contents {
        @NonNull
        private final Collection<Word> contents;
    }
}
