package io.gardenerframework.fragrans.api.security.automation.log.customizer;

import io.gardenerframework.fragrans.api.security.operator.schema.OperatorBrief;
import io.gardenerframework.fragrans.log.BasicLogger;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.LogMessageCustomizer;
import io.gardenerframework.fragrans.log.schema.content.Contents;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import io.gardenerframework.fragrans.log.schema.template.GenericOperationLogTemplate;
import io.gardenerframework.fragrans.log.schema.template.Template;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.springframework.beans.BeanUtils;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


@AllArgsConstructor
public class GenericOperationLoggerMessageCustomizer implements LogMessageCustomizer {
    private final OperatorBrief operatorBrief;

    @Override
    public boolean support(BasicLogger logger, Template template, Contents contents) {
        return logger instanceof GenericOperationLogger && RequestContextHolder.getRequestAttributes() != null;
    }

    @Override
    public Template customize(Template template) {
        return new GenericOperationAuditLogTemplate((GenericOperationLogTemplate) template);
    }

    @Override
    public Contents customize(Contents contents) {
        return new GenericOperationAuditLogContent(GenericOperationLogContent.builder(), (GenericOperationLogContent) contents, operatorBrief);
    }

    @AllArgsConstructor
    public static class GenericOperationAuditLogTemplate extends GenericOperationLogTemplate {
        @NonNull
        @Delegate
        private final GenericOperationLogTemplate template;

        @Override
        public String toString() {
            return template + ", 操作方: {}";
        }
    }

    public static class GenericOperationAuditLogContent extends GenericOperationLogContent {
        @Delegate(excludes = Contents.class)
        @NonNull
        private final GenericOperationLogContent target;
        @NonNull
        @Getter
        private final OperatorDetail operatorDetail;

        protected GenericOperationAuditLogContent(GenericOperationLogContentBuilder<?, ?> b, @NonNull GenericOperationLogContent target, @NonNull OperatorBrief operatorBrief) {
            super(b);
            this.target = target;
            this.operatorDetail = new OperatorDetail();
            BeanUtils.copyProperties(operatorBrief, operatorDetail);
        }

        @Override
        public Collection<Word> getContents() {
            List<Word> contents = new LinkedList<>(target.getContents());
            contents.add(new Word() {
                @Override
                public String toString() {
                    return String.format("[%s]",
                            String.join(", ", operatorDetail.getPairs()));
                }
            });
            return contents;
        }
    }

    @Getter
    @Setter
    public static class OperatorDetail extends OperatorBrief implements Detail {
    }
}
