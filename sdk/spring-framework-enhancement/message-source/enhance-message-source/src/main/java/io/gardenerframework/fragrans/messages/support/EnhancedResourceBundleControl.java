package io.gardenerframework.fragrans.messages.support;

import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.reason.AlreadyExisted;
import io.gardenerframework.fragrans.log.common.schema.reason.NotFound;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Register;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import io.gardenerframework.fragrans.messages.configuration.EnhanceMessageSourceComponent;
import io.gardenerframework.fragrans.messages.resource.annotation.ResourceFormat;
import io.gardenerframework.fragrans.messages.resource.loader.ResourceBundleLoader;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 定义一个内部类来读取资源文件
 * <p>
 * 之所以是内部类，是因为资源文件读取时也有缓存时间和默认字符集，需要将这两个属性和MessageSource的设定值保持统一
 * <p>
 * 而MessageSource的设定值读取时是protected方法，因此需要内部类访问外部类的开发模式
 */
@Slf4j
@RequiredArgsConstructor
@EnhanceMessageSourceComponent
class EnhancedResourceBundleControl extends ResourceBundle.Control implements InitializingBean {
    private final GenericBasicLogger basicLogger = GenericLoggers.basicLogger();
    private final GenericOperationLogger operationLogger = GenericLoggers.operationLogger();
    private final Collection<ResourceBundleLoader> resourceBundleLoaders;
    /**
     * 声明一个格式与加载器的注册表
     */
    private final Map<String, ResourceBundleLoader> loaderRegistry = new ConcurrentHashMap<>(10);
    @Setter
    private EnhancedMessageSourceSupport owner;

    /**
     * 在父类的基础上增加支持yaml/yml两种格式
     * <p>
     * 声明支持yaml格式
     *
     * @param baseName 资源包名
     * @return 支持的格式
     */
    @Override
    public List<String> getFormats(String baseName) {
        List<String> formats = new ArrayList<>(super.getFormats(baseName));
        formats.addAll(loaderRegistry.keySet());
        return Collections.unmodifiableList(formats);
    }

    /**
     * 读取资源文件
     *
     * @param baseName 资源包名
     * @param locale   当前地区
     * @param format   格式
     * @param loader   类加载器
     * @param reload   是否重新读取
     * @return 资源包
     * @throws IllegalAccessException 无法访问
     * @throws InstantiationException 无法实例化
     * @throws IOException            读取错误
     */
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        ResourceBundleLoader resourceBundleLoader = loaderRegistry.get(format);
        Assert.notNull(format, "unsupported format " + format);
        try {
            return resourceBundleLoader.load(baseName, toBundleName(baseName, locale), locale, owner.getDefaultEncoding(), loader, reload);
        } catch (Exception exception) {

            throw new RuntimeException(exception);
        }
    }

    /**
     * 由于父类具备自己的默认语言环境设置，因此重写方法以免与预期不符<br>
     * 其实这个默认的语言环境基本等价于系统默认值
     *
     * @param baseName 资源包名
     * @param locale   当前语言环境
     * @return 当搜索失败时使用的语言环境
     */
    @Override
    @Nullable
    public Locale getFallbackLocale(String baseName, Locale locale) {
        Locale defaultLocale = owner.getDefaultLocale();
        return (defaultLocale != null && !defaultLocale.equals(locale) ? defaultLocale : null);
    }

    /**
     * 由于父类是自己有缓存去控制时间，因此需要重写获取缓存有效期的方法
     *
     * @param baseName 资源包名
     * @param locale   当前语言环境
     * @return 有效期
     */
    @Override
    public long getTimeToLive(String baseName, Locale locale) {
        long cacheMillis = owner.getCacheMillis();
        return (cacheMillis >= 0 ? cacheMillis : super.getTimeToLive(baseName, locale));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //注册所有loader
        resourceBundleLoaders.forEach(
                loader -> {
                    ResourceFormat annotation = AnnotationUtils.findAnnotation(loader.getClass(), ResourceFormat.class);
                    if (annotation != null) {
                        if (loaderRegistry.get(annotation.value()) != null) {
                            basicLogger.error(
                                    log,
                                    GenericBasicLogContent.builder().what(ResourceBundleLoader.class).how(new AlreadyExisted()).detail(new Detail() {
                                        private final String format = annotation.value();
                                    }).build(),
                                    null
                            );
                            throw new IllegalStateException("duplicate format " + annotation.value());
                        }
                        loaderRegistry.put(annotation.value(), loader);
                    } else {
                        basicLogger.debug(log, GenericBasicLogContent.builder().what(ResourceFormat.class).how(new NotFound()).detail(new Detail() {
                            private final Class<? extends ResourceBundleLoader> loaderClass = loader.getClass();
                        }).build(), null);
                    }
                }
        );
        operationLogger.info(
                log,
                GenericOperationLogContent.builder().what(ResourceBundleLoader.class).operation(new Register()).state(new Done()).detail(new Detail() {
                    private final Collection<Class<? extends ResourceBundleLoader>> loaders = resourceBundleLoaders.stream().map(ResourceBundleLoader::getClass).collect(Collectors.toSet());
                }).build(),
                null
        );
    }
}
