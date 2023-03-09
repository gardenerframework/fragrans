package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.template.GenericOperationLogTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/6/8 6:54 下午
 */
@Component
public class GenericOperationLogger extends AbstractGenericLogger<GenericOperationLogTemplate, GenericOperationLogContent> {
    public GenericOperationLogger() {
        this.setTemplate(new GenericOperationLogTemplate());
    }
}
