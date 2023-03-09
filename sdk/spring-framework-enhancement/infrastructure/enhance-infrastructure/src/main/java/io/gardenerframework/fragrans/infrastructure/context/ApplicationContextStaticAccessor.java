package io.gardenerframework.fragrans.infrastructure.context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

public class ApplicationContextStaticAccessor implements SpringApplicationRunListener {

    private static ConfigurableApplicationContext applicationContext;

    public ApplicationContextStaticAccessor(SpringApplication application, String[] args) {

    }

    public static ConfigurableApplicationContext applicationContext() {
        Assert.notNull(applicationContext, "ApplicationContext is not initialized");
        return applicationContext;
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        applicationContext = context;
    }
}
