package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.event.LogEvent;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.template.AbstractGenericTemplate;
import io.gardenerframework.fragrans.log.schema.template.GenericBasicLogTemplate;
import io.gardenerframework.fragrans.log.schema.word.Word;
import org.slf4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author zhanghan30
 * @date 2022/6/8 6:54 下午
 */
@Component
public class GenericBasicLogger extends AbstractGenericLogger<GenericBasicLogContent> {
    private final AbstractGenericTemplate template = new GenericBasicLogTemplate();

    @Override
    protected void writeInternally(BasicLoggerMethodTemplate method, Logger logger, GenericBasicLogContent content, Throwable cause) {
        method.log(logger, template, Arrays.asList(new TargetClassWrapper(content.getWhat()), content.getHow(), new DetailWrapper(content.getDetail())), cause);
    }

    @Nullable
    @Override
    public GenericBasicLogContent unwrapContent(LogEvent event) {
        if (event.getTemplate() instanceof GenericBasicLogTemplate) {
            Word[] words = event.getWords().toArray(new Word[]{});
            if (words.length != 3) {
                return null;
            }
            return GenericBasicLogContent.builder()
                    .what(((TargetClassWrapper) words[0]).getWrapped())
                    .how(words[1])
                    .detail(((DetailWrapper) words[2]).getWrapped())
                    .build();
        } else {
            return null;
        }
    }
}
