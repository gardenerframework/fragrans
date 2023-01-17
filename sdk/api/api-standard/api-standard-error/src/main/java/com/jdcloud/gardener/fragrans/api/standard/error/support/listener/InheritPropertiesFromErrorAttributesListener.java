package com.jdcloud.gardener.fragrans.api.standard.error.support.listener;

import com.jdcloud.gardener.fragrans.api.standard.error.support.DefaultApiErrorFactory;
import com.jdcloud.gardener.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import com.jdcloud.gardener.fragrans.api.standard.schema.ApiError;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Priority;
import java.util.Date;
import java.util.Map;

/**
 * 从错误属性继承一些已有的值
 *
 * @author zhanghan30
 * @date 2022/5/9 6:04 下午
 */
@Component
@Priority(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnBean(DefaultApiErrorFactory.class)
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
