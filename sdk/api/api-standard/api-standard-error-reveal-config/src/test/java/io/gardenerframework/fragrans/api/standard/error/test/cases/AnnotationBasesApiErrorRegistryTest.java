package io.gardenerframework.fragrans.api.standard.error.test.cases;

import io.gardenerframework.fragrans.api.standard.error.configuration.ApiErrorRegistry;
import io.gardenerframework.fragrans.api.standard.error.test.ApiStandardErrorRevealTestApplication;
import io.gardenerframework.fragrans.api.standard.error.test.cases.exception.annotation.hide.AnnotationHideEx1;
import io.gardenerframework.fragrans.api.standard.error.test.cases.exception.annotation.hide.AnnotationHideEx2;
import io.gardenerframework.fragrans.api.standard.error.test.cases.exception.annotation.hide.ShouldHideEx;
import io.gardenerframework.fragrans.api.standard.error.test.cases.exception.annotation.hide.ShouldRevealEx;
import io.gardenerframework.fragrans.api.standard.error.test.cases.exception.annotation.revealed.AnnotationRevealedEx1;
import io.gardenerframework.fragrans.api.standard.error.test.cases.exception.annotation.revealed.AnnotationRevealedEx2;
import io.gardenerframework.fragrans.api.standard.error.test.cases.exception.base.hide.BaseClassHideEx1;
import io.gardenerframework.fragrans.api.standard.error.test.cases.exception.base.hide.BaseClassHideEx2;
import io.gardenerframework.fragrans.api.standard.error.test.cases.exception.base.revealed.BaseClassRevealedEx1;
import io.gardenerframework.fragrans.api.standard.error.test.cases.exception.base.revealed.BaseClassRevealedEx2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhanghan30
 * @date 2022/8/26 9:22 上午
 */
@SpringBootTest(classes = ApiStandardErrorRevealTestApplication.class)
public class AnnotationBasesApiErrorRegistryTest {
    @Autowired
    private ApiErrorRegistry apiErrorRegistry;

    @Test
    @DisplayName("冒烟测试")
    public void smokeTest() {
        //测试包的部分
        //测试展示
        Exception exception = new AnnotationRevealedEx1();
        Assertions.assertTrue(apiErrorRegistry.isErrorRevealed(exception));
        exception = new AnnotationRevealedEx2();
        Assertions.assertTrue(apiErrorRegistry.isErrorRevealed(exception));
        exception = new BaseClassRevealedEx1();
        Assertions.assertTrue(apiErrorRegistry.isErrorRevealed(exception));
        exception = new BaseClassRevealedEx2();
        Assertions.assertTrue(apiErrorRegistry.isErrorRevealed(exception));
        exception = new ShouldRevealEx();
        Assertions.assertTrue(apiErrorRegistry.isErrorRevealed(exception));
        //测试隐藏
        exception = new AnnotationHideEx1();
        Assertions.assertFalse(apiErrorRegistry.isErrorRevealed(exception));
        exception = new AnnotationHideEx2();
        Assertions.assertFalse(apiErrorRegistry.isErrorRevealed(exception));
        exception = new ShouldHideEx();
        Assertions.assertFalse(apiErrorRegistry.isErrorRevealed(exception));
        exception = new BaseClassHideEx1();
        Assertions.assertFalse(apiErrorRegistry.isErrorRevealed(exception));
        exception = new BaseClassHideEx2();
        Assertions.assertFalse(apiErrorRegistry.isErrorRevealed(exception));
    }
}
