package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.template.GenericBasicLogTemplate;

/**
 * @author zhanghan30
 * @date 2022/6/8 6:54 下午
 */
public class GenericBasicLogger extends AbstractGenericLogger<GenericBasicLogTemplate, GenericBasicLogContent> {
    public GenericBasicLogger() {
        this.setTemplate(new GenericBasicLogTemplate());
    }
}
