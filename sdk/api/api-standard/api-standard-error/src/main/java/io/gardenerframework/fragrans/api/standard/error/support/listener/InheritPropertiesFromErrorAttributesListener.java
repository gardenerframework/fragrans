package io.gardenerframework.fragrans.api.standard.error.support.listener;

import io.gardenerframework.fragrans.api.standard.error.configuration.ApiStandardErrorComponent;
import io.gardenerframework.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import io.gardenerframework.fragrans.api.standard.schema.ApiError;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import javax.annotation.Priority;
import java.util.Date;
import java.util.Map;

/**
 * 从错误属性继承一些已有的值
 *
 * @author zhanghan30
 * @date 2022/5/9 6:04 下午
 */
@ApiStandardErrorComponent
@Priority(Ordered.HIGHEST_PRECEDENCE)
public class InheritPropertiesFromErrorAttributesListener implements ApplicationListener<InitializingApiErrorPropertiesEvent> {
    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        ApiError apiError = event.getApiError();
        Map<String, Object> errorAttributes = event.getErrorAttributes();
        apiError.setUri((String) errorAttributes.get("path"));
        apiError.setReason((String) errorAttributes.get("error"));
        apiError.setStatus(errorAttributes.get("status") == null ? 0 : (Integer) errorAttributes.get("status"));
        apiError.setTimestamp(new Date());
    }
}
