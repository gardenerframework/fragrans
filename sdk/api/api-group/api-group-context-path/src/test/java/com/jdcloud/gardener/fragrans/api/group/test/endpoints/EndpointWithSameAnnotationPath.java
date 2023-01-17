package com.jdcloud.gardener.fragrans.api.group.test.endpoints;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhanghan30
 * @date 2022/8/21 11:30 下午
 */
@RequestMapping("/test-same")
@TestAnnotation
@Component
@RestController
public class EndpointWithSameAnnotationPath {
    @GetMapping
    public String ok() {
        return "OK";
    }
}
