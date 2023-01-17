package com.jdcloud.gardener.fragrans.api.test.endpoints;

import com.jdcloud.gardener.fragrans.api.advice.engine.EndpointHandlerMethodBeforeAdviceAdapter;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/11/16 15:19
 */
@Component
public class EndpointHandlerMethodBeforeAdviceAdapterEndpointAdviceWithMatchType extends EndpointHandlerMethodBeforeAdviceAdapter {
    public EndpointHandlerMethodBeforeAdviceAdapterEndpointAdviceWithMatchType() {
        //关注的时自己
        super(EndpointHandlerMethodBeforeAdviceAdapterEndpointAdviceWithMatchType.class);
    }

    public void nonTest() {
        throw new RuntimeException();
    }
}
