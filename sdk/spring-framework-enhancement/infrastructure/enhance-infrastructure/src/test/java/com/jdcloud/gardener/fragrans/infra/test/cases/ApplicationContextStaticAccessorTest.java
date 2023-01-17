package com.jdcloud.gardener.fragrans.infra.test.cases;

import com.jdcloud.gardener.fragrans.infra.test.SpringInfraFrameworkEnhanceTestApplication;
import com.jdcloud.gardener.fragrans.infrastructure.context.ApplicationContextStaticAccessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest(classes = SpringInfraFrameworkEnhanceTestApplication.class)
public class ApplicationContextStaticAccessorTest {
    @Test
    public void smokeTest() {
        ConfigurableApplicationContext configurableApplicationContext = ApplicationContextStaticAccessor.applicationContext();
    }
}
