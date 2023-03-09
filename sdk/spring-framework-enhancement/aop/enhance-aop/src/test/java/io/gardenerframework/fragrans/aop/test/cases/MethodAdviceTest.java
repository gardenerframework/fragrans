package io.gardenerframework.fragrans.aop.test.cases;

import io.gardenerframework.fragrans.aop.test.EnhanceAopApplication;
import io.gardenerframework.fragrans.aop.test.advice.SampleClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = EnhanceAopApplication.class)
public class MethodAdviceTest {
    @Autowired
    private SampleClass sampleClass;

    @Test
    public void smokeTest() {
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> sampleClass.method()
        );
    }
}
