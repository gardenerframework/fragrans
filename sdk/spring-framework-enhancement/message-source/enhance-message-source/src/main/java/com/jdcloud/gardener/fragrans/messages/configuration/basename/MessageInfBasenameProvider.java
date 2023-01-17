package com.jdcloud.gardener.fragrans.messages.configuration.basename;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * @author ZhangHan
 * @date 2022/6/10 9:51
 */
@Component
public class MessageInfBasenameProvider implements BasenameProvider {
    private static final String MESSAGE_INF_DIR_NAME = "MESSAGE-INF";
    private static final String BASENAME_KEY = "com.jdcloud.gardener.fragrans.messages.basenames";


    @Override
    public Set<String> getBasenames() throws Exception {
        //如果旧文件存在就会失败
        failOnLegacyManifest();
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        List<String> suffixes = Collections.unmodifiableList(Arrays.asList("yaml", "yml"));
        Set<String> basenames = new HashSet<>();
        String manifestFile = "basenames";
        //读取2种类型的后缀
        for (String suffix : suffixes) {
            String pattern = String.format("classpath*:%s/%s.%s", MESSAGE_INF_DIR_NAME, manifestFile, suffix);
            Resource[] resources = new PathMatchingResourcePatternResolver(ClassUtils.getDefaultClassLoader())
                    .getResources(pattern);

            for (Resource resource : resources) {
                if (resource.exists()) {
                    //当前资源存在，则打开资源读取其中的配置
                    yamlPropertiesFactoryBean.setResources(resource);
                    Properties properties = yamlPropertiesFactoryBean.getObject();
                    assert properties != null;
                    //将配置
                    basenames.addAll(readBasenamesFromProperties(properties));
                }
            }
        }
        return basenames;
    }

    /**
     * 读取名称
     *
     * @param properties 属性
     * @return 基础消息资源名称
     */
    private Set<String> readBasenamesFromProperties(Properties properties) {
        //获取所有key
        Enumeration<Object> keys = properties.keys();
        Set<String> basenames = new HashSet<>();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            if (key.startsWith(BASENAME_KEY)) {
                basenames.add(properties.get(key).toString());
            }
        }
        return basenames;
    }

    /**
     * 检查是否存在之前的manifest
     *
     * @throws Exception 出现问题
     */
    private void failOnLegacyManifest() throws Exception {
        List<String> suffixes = Collections.unmodifiableList(Arrays.asList("yaml", "yml"));
        String manifestFile = "basenames";
        //读取2种类型的后缀
        for (String suffix : suffixes) {
            String pattern = String.format("classpath*:%s/%s.%s", "META-INF", manifestFile, suffix);
            Resource[] resources = new PathMatchingResourcePatternResolver(ClassUtils.getDefaultClassLoader())
                    .getResources(pattern);
            for (Resource resource : resources) {
                if (resource.exists()) {
                    throw new UnsupportedOperationException("migrate META-INF/basenames to MESSAGE-INF/basenames");
                }
            }
        }
    }
}
