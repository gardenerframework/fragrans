package io.gardenerframework.fragrans.api.security.automation.log.customizer;

import io.gardenerframework.fragrans.api.security.operator.schema.OperatorBrief;
import io.gardenerframework.fragrans.log.BasicLogger;
import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.LogMessageCustomizer;
import io.gardenerframework.fragrans.log.schema.content.Content;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import io.gardenerframework.fragrans.log.schema.template.GenericOperationLogTemplate;
import io.gardenerframework.fragrans.log.schema.template.Template;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


@AllArgsConstructor
public class GenericOperationLoggerMessageCustomizer implements LogMessageCustomizer {
    private final OperatorBrief operatorBrief;

    @Override
    public boolean support(BasicLogger logger, Template template, Content content) {
        return (logger instanceof GenericOperationLogger || logger instanceof GenericBasicLogger) && RequestContextHolder.getRequestAttributes() != null;
    }

    @Override
    public Template customize(Template template) {
        return new GenericOperationAuditLogTemplate((GenericOperationLogTemplate) template);
    }

    @Override
    public Content customize(Content content) {
        return new GenericOperationAuditLogContent(content, operatorBrief);
    }

    @AllArgsConstructor
    @Getter
    public static class GenericOperationAuditLogTemplate implements Template {
        @NonNull
        private final GenericOperationLogTemplate template;

        @Override
        public String toString() {
            return template + ", 操作方: {}";
        }
    }

    @Getter
    public static class GenericOperationAuditLogContent implements Content {
        @NonNull
        private final Content target;
        @NonNull
        private final OperatorDetail operatorDetail;

        protected GenericOperationAuditLogContent(@NonNull Content target, @NonNull OperatorBrief operatorBrief) {
            this.target = target;
            this.operatorDetail = new OperatorDetail();
            BeanUtils.copyProperties(operatorBrief, operatorDetail);
        }

        @Override
        public Collection<Word> getContent() {
            List<Word> content = new LinkedList<>(target.getContent());
            content.add(new Word() {
                @Override
                public String toString() {
                    return String.format("[%s]",
                            String.join(", ", operatorDetail.getPairs()));
                }
            });
            return content;
        }
    }

    @Getter
    @Setter
    public static class OperatorDetail extends OperatorBrief implements Detail {
    }
}
