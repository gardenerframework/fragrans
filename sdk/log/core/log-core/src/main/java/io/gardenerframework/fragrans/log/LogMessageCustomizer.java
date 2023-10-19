package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.schema.content.Content;
import io.gardenerframework.fragrans.log.schema.template.Template;

public interface LogMessageCustomizer {
    /**
     * 是否支持模板和内容
     *
     * @param logger   日志记录器，用来查看还不是关注的类型
     * @param template 模板
     * @param content  内容
     * @return 是否支持
     */
    boolean support(BasicLogger logger, Template template, Content content);

    /**
     * 处理模板
     *
     * @param template 模板
     * @return 处理完的模板
     */
    Template customize(Template template);

    /**
     * 处理内容
     *
     * @param content 内容
     * @return 处理完的内容
     */
    Content customize(Content content);
}
