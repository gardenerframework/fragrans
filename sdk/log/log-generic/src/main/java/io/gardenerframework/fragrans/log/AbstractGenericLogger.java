package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.schema.content.AbstractGenericLogContent;
import io.gardenerframework.fragrans.log.schema.template.AbstractGenericTemplate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.lang.Nullable;

/**
 * @author ZhangHan
 * @date 2022/6/9 1:26
 */
public abstract class AbstractGenericLogger<T extends AbstractGenericTemplate, C extends AbstractGenericLogContent> extends BasicLogger {

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private T template;

    /**
     * 写debug日志
     *
     * @param logger  日志记录类
     * @param content 日志内容
     * @param cause   异常
     */
    public void debug(Logger logger, C content, @Nullable Throwable cause) {
        debug(logger, template, content, cause);
    }

    /**
     * 写info日志
     *
     * @param logger  日志记录类
     * @param content 日志内容
     * @param cause   异常
     */
    public void info(Logger logger, C content, @Nullable Throwable cause) {
        info(logger, template, content, cause);
    }

    /**
     * 写warn日志
     *
     * @param logger  日志记录类
     * @param content 日志内容
     * @param cause   异常
     */
    public void warn(Logger logger, C content, @Nullable Throwable cause) {
        warn(logger, template, content, cause);
    }

    /**
     * 写error日志
     *
     * @param logger  日志记录类
     * @param content 日志内容
     * @param cause   异常
     */
    public void error(Logger logger, C content, @Nullable Throwable cause) {
        error(logger, template, content, cause);
    }
}
