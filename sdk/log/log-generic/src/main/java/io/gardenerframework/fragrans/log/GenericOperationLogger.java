package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.event.LogEvent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.OperatorDetail;
import io.gardenerframework.fragrans.log.schema.template.AbstractGenericTemplate;
import io.gardenerframework.fragrans.log.schema.template.GenericOperationLogTemplate;
import io.gardenerframework.fragrans.log.schema.template.GenericOperationTracingLogTemplate;
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
public class GenericOperationLogger extends AbstractGenericLogger<GenericOperationLogContent> {
    private final AbstractGenericTemplate template = new GenericOperationLogTemplate();
    private final AbstractGenericTemplate tracingTemplate = new GenericOperationTracingLogTemplate();

    @Override
    protected void writeInternally(BasicLoggerMethodTemplate method, Logger logger, GenericOperationLogContent content, Throwable cause) {
        method.log(logger, content.getOperator() == null ? template : tracingTemplate, Arrays.asList(new TargetClassWrapper(content.getWhat()), content.getOperation(), content.getState(), new DetailWrapper(content.getDetail()), new DetailWrapper(content.getOperator())), cause);
    }

    @Nullable
    @Override
    public GenericOperationLogContent unwrapContent(LogEvent event) {
        if (event.getTemplate() instanceof GenericOperationLogTemplate) {
            Word[] words = event.getWords().toArray(new Word[]{});
            if (words.length < 4) {
                return null;
            }
            return GenericOperationLogContent.builder()
                    .what(((TargetClassWrapper) words[0]).getWrapped())
                    .operation(words[1])
                    .state(words[2])
                    .detail(((DetailWrapper) words[3]).getWrapped())
                    .operator(words.length > 4 ? (OperatorDetail) ((DetailWrapper) words[4]).getWrapped() : null)
                    .build();
        } else {
            return null;
        }
    }
}
