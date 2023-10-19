package io.gardenerframework.fragrans.log.test.log;

import io.gardenerframework.fragrans.log.BasicLogger;
import io.gardenerframework.fragrans.log.LogMessageCustomizer;
import io.gardenerframework.fragrans.log.schema.content.Content;
import io.gardenerframework.fragrans.log.schema.template.Template;
import org.springframework.stereotype.Component;

@Component
public class TestLogMessageCustomizer implements LogMessageCustomizer {

    @Override
    public boolean support(BasicLogger logger, Template template, Content content) {
        return true;
    }

    @Override
    public Template customize(Template template) {
        return template;
    }

    @Override
    public Content customize(Content content) {
        return content;
    }
}
