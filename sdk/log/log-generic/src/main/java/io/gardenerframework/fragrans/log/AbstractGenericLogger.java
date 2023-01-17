package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.annotation.LogTarget;
import io.gardenerframework.fragrans.log.annotation.ReferLogTarget;
import io.gardenerframework.fragrans.log.event.LogEvent;
import io.gardenerframework.fragrans.log.schema.content.AbstractGenericLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

/**
 * @author ZhangHan
 * @date 2022/6/9 1:26
 */
public abstract class AbstractGenericLogger<C extends AbstractGenericLogContent> extends BasicLogger {

    /**
     * 写debug日志
     *
     * @param logger  日志记录类
     * @param content 日志内容
     * @param cause   异常
     */
    public void debug(Logger logger, C content, @Nullable Throwable cause) {
        writeInternally(super::debug, logger, content, cause);
    }

    /**
     * 写info日志
     *
     * @param logger  日志记录类
     * @param content 日志内容
     * @param cause   异常
     */
    public void info(Logger logger, C content, @Nullable Throwable cause) {
        writeInternally(super::info, logger, content, cause);
    }

    /**
     * 写warn日志
     *
     * @param logger  日志记录类
     * @param content 日志内容
     * @param cause   异常
     */
    public void warn(Logger logger, C content, @Nullable Throwable cause) {
        writeInternally(super::warn, logger, content, cause);
    }

    /**
     * 写error日志
     *
     * @param logger  日志记录类
     * @param content 日志内容
     * @param cause   异常
     */
    public void error(Logger logger, C content, @Nullable Throwable cause) {
        writeInternally(super::error, logger, content, cause);
    }

    /**
     * 真正的书写方法
     *
     * @param method  使用的log的方法引用
     * @param logger  日志记录
     * @param content 内容
     * @param cause   异常
     */
    protected abstract void writeInternally(BasicLoggerMethodTemplate method, Logger logger, C content, Throwable cause);

    /**
     * 从事件中解包出日志内容
     *
     * @param event 事件
     * @return 日志内容
     */
    @Nullable
    public abstract C unwrapContent(LogEvent event);

    @Getter
    @AllArgsConstructor
    protected static class TargetClassWrapper implements Word {
        private final Class<?> wrapped;

        @Override
        public String toString() {
            return getLogTarget(wrapped);
        }

        /**
         * 从带有@LogTarget的类中提取具体的名称
         *
         * @return 日志目标
         */
        private String getLogTarget(Class<?> targetClass) {
            ReferLogTarget referLogTarget = AnnotationUtils.findAnnotation(targetClass, ReferLogTarget.class);
            //优先处理引用的类
            if (referLogTarget != null && referLogTarget.value() != null) {
                String referredText = getLogTarget(referLogTarget.value());
                return String.format(
                        "%s%s%s",
                        referLogTarget.prefix() == null ? "" : referLogTarget.prefix(),
                        referredText,
                        referLogTarget.suffix() == null ? "" : referLogTarget.suffix()
                );
            }
            //处理LogTarget注解
            LogTarget annotation = AnnotationUtils.findAnnotation(targetClass, LogTarget.class);
            if (annotation == null) {
                //使用类名
                return targetClass.getSimpleName();
            } else {
                return annotation.value();
            }
        }
    }

    @Getter
    @AllArgsConstructor
    protected static class DetailWrapper implements Word {
        private final Detail wrapped;

        @Override
        public String toString() {
            return wrapped == null ? "" : String.format("[%s]", String.join(", ", wrapped.getPairs()));
        }
    }
}
