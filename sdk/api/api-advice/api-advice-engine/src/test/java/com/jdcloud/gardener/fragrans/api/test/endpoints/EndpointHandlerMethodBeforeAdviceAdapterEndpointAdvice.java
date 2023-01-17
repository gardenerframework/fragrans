package com.jdcloud.gardener.fragrans.api.test.endpoints;

import com.jdcloud.gardener.fragrans.api.advice.engine.EndpointHandlerMethodBeforeAdviceAdapter;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/7/7 5:56 下午
 */
@Component
public class EndpointHandlerMethodBeforeAdviceAdapterEndpointAdvice extends EndpointHandlerMethodBeforeAdviceAdapter {
    public EndpointHandlerMethodBeforeAdviceAdapterEndpointAdvice() {
        //关注的接口类型
        super(EndpointHandlerMethodBeforeAdviceAdapterEndpoint.class);
    }

    public void test() {
        throw new RuntimeException();
    }
}
