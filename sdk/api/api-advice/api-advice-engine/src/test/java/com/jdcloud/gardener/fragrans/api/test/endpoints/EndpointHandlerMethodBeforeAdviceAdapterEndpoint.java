package com.jdcloud.gardener.fragrans.api.test.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhanghan30
 * @date 2022/7/7 5:57 下午
 */
@RestController
@RequestMapping("/EndpointHandlerMethodBeforeAdviceAdapterEndpoint")
public class EndpointHandlerMethodBeforeAdviceAdapterEndpoint {
    @GetMapping
    public void test() {

    }

    @PostMapping
    public void nonTest() {

    }
}
