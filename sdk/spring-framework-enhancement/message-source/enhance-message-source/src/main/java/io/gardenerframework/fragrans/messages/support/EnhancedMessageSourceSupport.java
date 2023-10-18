package io.gardenerframework.fragrans.messages.support;

import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Register;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import io.gardenerframework.fragrans.messages.configuration.EnhanceMessageSourceComponent;
import io.gardenerframework.fragrans.messages.configuration.basename.BasenameProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * 实现的message source
 *
 * @author zhanghan30
 * @date 2022/6/9 5:48 下午
 */
@EnhanceMessageSourceComponent
@Primary
@Slf4j
@AllArgsConstructor
public class EnhancedMessageSourceSupport extends ResourceBundleMessageSource implements EnhancedMessageSource, InitializingBean {
    private final MessageSourceProperties messageSourceProperties;
    private final EnhancedResourceBundleControl control;
    private final Collection<BasenameProvider> basenameProviders;
    private final GenericOperationLogger operationLogger = GenericLoggers.operationLogger();


    /**
     * 完成资源包的加载
     *
     * @param basename 资源基础名称
     * @param locale   语言环境
     * @return 资源文件
     * @throws MissingResourceException 找不到资源
     */
    @Override
    protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
        ClassLoader classLoader = getBundleClassLoader();
        Assert.state(classLoader != null, "No bundle ClassLoader set");
        return ResourceBundle.getBundle(basename, locale, classLoader, control);
    }

    @Override
    public String getDefaultEncoding() {
        return StandardCharsets.UTF_8.name();
    }

    @Override
    public long getCacheMillis() {
        return super.getCacheMillis();
    }

    @Nullable
    @Override
    public Locale getDefaultLocale() {
        return super.getDefaultLocale();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        control.setOwner(this);
        applyMessageSourceProperties();
        applyProvidedBasenames();
    }

    /**
     * 应用属性文件的设置要求
     */
    private void applyMessageSourceProperties() {
        if (StringUtils.hasText(messageSourceProperties.getBasename())) {
            this.addBasenames(StringUtils
                    .commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(messageSourceProperties.getBasename())));
        }
        this.setFallbackToSystemLocale(messageSourceProperties.isFallbackToSystemLocale());
        Duration cacheDuration = messageSourceProperties.getCacheDuration();
        if (cacheDuration != null) {
            this.setCacheMillis(cacheDuration.toMillis());
        }
        this.setAlwaysUseMessageFormat(messageSourceProperties.isAlwaysUseMessageFormat());
        this.setUseCodeAsDefaultMessage(messageSourceProperties.isUseCodeAsDefaultMessage());
    }

    /**
     * 应用manifest的设置
     */
    private void applyProvidedBasenames() throws Exception {
        for (BasenameProvider basenameProvider : basenameProviders) {
            this.addBasenames(basenameProvider.getBasenames().toArray(new String[]{}));
        }
        Set<String> basenameSet = this.getBasenameSet();
        operationLogger.info(
                log,
                GenericOperationLogContent.builder().what(
                        BasenameProvider.class
                ).operation(new Register()).state(new Done()).detail(new Detail() {
                    private final Set<String> basenames = basenameSet;
                }).build(),
                null
        );
    }
}
