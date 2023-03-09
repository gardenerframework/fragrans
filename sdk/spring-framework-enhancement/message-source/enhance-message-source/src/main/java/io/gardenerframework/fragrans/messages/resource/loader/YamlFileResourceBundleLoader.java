package io.gardenerframework.fragrans.messages.resource.loader;

import io.gardenerframework.fragrans.messages.resource.annotation.ResourceFormat;
import io.gardenerframework.fragrans.messages.resource.utils.ResourceUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZhangHan
 * @date 2022/6/10 1:06
 */
@ResourceFormat("file.yaml")
@Component
public class YamlFileResourceBundleLoader implements ResourceBundleLoader {
    @Nullable
    @Override
    public ResourceBundle load(String baseName, String bundleName, Locale locale, String charset, ClassLoader loader, boolean reload) throws Exception {
        //读取yaml格式的资源
        List<String> suffixes = Arrays.asList("yaml", "yml");
        for (String suffix : suffixes) {
            String resourceBundleName = ResourceUtils.toResourceName(bundleName, suffix);
            ClassPathResource resource = new ClassPathResource(resourceBundleName);
            if (resource.exists()) {
                YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
                yamlPropertiesFactoryBean.setResources(resource);
                Properties loaded = yamlPropertiesFactoryBean.getObject();
                return new ResourceBundle() {
                    private final Properties properties = loaded;

                    @Override
                    protected Object handleGetObject(String key) {
                        return properties.getProperty(key);
                    }

                    @Override
                    public Enumeration<String> getKeys() {
                        return Collections.enumeration(Objects.requireNonNull(loaded).keySet().stream().map(Object::toString).collect(Collectors.toSet()));
                    }
                };
            }
        }
        return null;
    }
}
