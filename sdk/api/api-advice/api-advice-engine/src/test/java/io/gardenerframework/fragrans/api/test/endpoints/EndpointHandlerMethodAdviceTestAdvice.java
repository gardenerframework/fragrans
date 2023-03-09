package io.gardenerframework.fragrans.api.test.endpoints;

import io.gardenerframework.fragrans.api.advice.engine.EndpointHandlerMethodAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;

/**
 * @author ZhangHan
 * @date 2022/5/14 2:06
 */
@Component
public class EndpointHandlerMethodAdviceTestAdvice implements EndpointHandlerMethodAdvice {
    @Override
    public void before(Object target, Method method, Object[] arguments) throws Exception {
        if (target instanceof EndpointHandlerMethodAdviceTestEndpoint) {
            throw new ForbiddenException();
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    private class ForbiddenException extends RuntimeException {

    }
}
