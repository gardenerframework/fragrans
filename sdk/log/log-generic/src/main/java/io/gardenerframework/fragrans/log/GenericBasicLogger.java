package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.template.GenericBasicLogTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/6/8 6:54 下午
 */
@Component
public class GenericBasicLogger extends AbstractGenericLogger<GenericBasicLogTemplate, GenericBasicLogContent> {
    public GenericBasicLogger() {
        this.setTemplate(new GenericBasicLogTemplate());
    }
}
