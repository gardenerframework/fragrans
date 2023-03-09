package io.gardenerframework.fragrans.api.group.test.endpoints;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhangHan
 * @date 2022/5/10 21:40
 */
@Component
@RestController
@TestAnnotation
public class ApiGroupWithRequestMappingOnMethodOnlyEndpoint {
    @GetMapping("/method")
    public void test() {

    }

    @GetMapping("/method/{id}")
    public String test(@PathVariable String id) {
        return id;
    }
}
