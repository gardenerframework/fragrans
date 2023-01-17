package com.jdcloud.gardener.fragrans.api.group.test.endpoints;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhangHan
 * @date 2022/5/10 21:40
 */
@RequestMapping("/type")
@Component
@RestController
@TestAnnotation
public class ApiGroupWithRequestMappingOnTypeEndpoint {
    @GetMapping("/method")
    public void test() {

    }

    @GetMapping("/method/{id}")
    public String test(@PathVariable String id) {
        return id;
    }
}
