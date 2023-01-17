package com.jdcloud.gardener.fragrans.api.standard.error.test;

import com.jdcloud.gardener.fragrans.api.standard.error.configuration.HideError;
import com.jdcloud.gardener.fragrans.api.standard.error.configuration.RevealError;
import com.jdcloud.gardener.fragrans.api.standard.error.test.cases.exception.annotation.hide.AnnotationHideEx1;
import com.jdcloud.gardener.fragrans.api.standard.error.test.cases.exception.annotation.hide.ShouldHideEx;
import com.jdcloud.gardener.fragrans.api.standard.error.test.cases.exception.annotation.revealed.AnnotationRevealedEx1;
import com.jdcloud.gardener.fragrans.api.standard.error.test.cases.exception.base.hide.BaseClassHideEx1;
import com.jdcloud.gardener.fragrans.api.standard.error.test.cases.exception.base.revealed.BaseClassRevealedEx1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhanghan30
 * @date 2022/8/26 9:20 上午
 */
@RevealError(
        basePackageClasses = AnnotationRevealedEx1.class,
        superClasses = {
                BaseClassRevealedEx1.class,
                ShouldHideEx.class
        }
)
@HideError(
        basePackageClasses = AnnotationHideEx1.class,
        superClasses = BaseClassHideEx1.class
)
@SpringBootApplication
public class ApiStandardErrorRevealTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiStandardErrorRevealTestApplication.class, args);
    }
}
