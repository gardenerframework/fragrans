package com.jdcloud.gardener.fragrans.api.test.endpoints;

import com.jdcloud.gardener.fragrans.api.advice.engine.EndpointHandlerMethodAdvice;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ZhangHan
 * @date 2022/5/14 2:06
 */
@Component
public class EndpointHandlerMethodAdviceTestAdvice implements EndpointHandlerMethodAdvice {
    @Override
    public void before(Object target, MethodSignature methodSignature, Object[] arguments) throws Exception {
        if (target instanceof EndpointHandlerMethodAdviceTestEndpoint) {
            throw new ForbiddenException();
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    private class ForbiddenException extends RuntimeException {

    }
}
