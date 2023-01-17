package com.jdcloud.gardener.fragrans.api.test.cases.endpoints;

import com.jdcloud.gardener.fragrans.api.standard.error.ServletApiErrorAttributes;
import com.jdcloud.gardener.fragrans.api.standard.error.ServletApiErrorAttributesConfigurer;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhanghan30
 * @date 2022/7/6 5:50 下午
 */
@RequestMapping("/controller/ignore")
@RestController
public class IgnoredController implements ServletApiErrorAttributesConfigurer {
    @GetMapping
    public void test() {
        throw new BadRequestException();
    }

    @Override
    public void accept(ServletApiErrorAttributes servletApiErrorAttributes) {
        servletApiErrorAttributes.getIgnoringUrlPatterns().add("/controller/ignore");
    }
}
