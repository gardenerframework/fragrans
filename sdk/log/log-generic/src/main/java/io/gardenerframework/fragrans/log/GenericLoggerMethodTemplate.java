package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.schema.content.AbstractGenericLogContent;
import org.slf4j.Logger;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/6/17 1:02 下午
 */
@FunctionalInterface
public interface GenericLoggerMethodTemplate<C extends AbstractGenericLogContent> {
    /**
     * 日志方法模板，用于给其它组件设置日志的登记
     *
     * @param logger  记录器
     * @param content 内容
     * @param cause   异常
     */
    void log(Logger logger, C content, @Nullable Throwable cause);
}
