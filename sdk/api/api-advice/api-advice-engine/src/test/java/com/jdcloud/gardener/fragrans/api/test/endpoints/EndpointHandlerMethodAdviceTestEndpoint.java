package com.jdcloud.gardener.fragrans.api.test.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhangHan
 * @date 2022/5/14 2:06
 */
@RestController
public class EndpointHandlerMethodAdviceTestEndpoint {
    @GetMapping("/EndpointHandlerMethodAdvice")
    public void testEndpointHandlerMethodAdvice() {

    }
}
