package io.gardenerframework.fragrans.messages.resource.loader;

import org.springframework.lang.Nullable;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author ZhangHan
 * @date 2022/6/9 23:56
 */
@FunctionalInterface
public interface ResourceBundleLoader {
    /**
     * 加载资源
     *
     * @param baseName   资源名
     * @param bundleName 推荐的资源包名称
     * @param locale     本地信息
     * @param charset    字符集
     * @param loader     类加载器
     * @param reload     是否是重新加载(意味着不应当从缓存中读取)
     * @return 加载后的资源，如果么有返回null
     * @throws Exception 加载过程中遇到的问题
     */
    @Nullable
    ResourceBundle load(String baseName, String bundleName, Locale locale, String charset, ClassLoader loader, boolean reload) throws Exception;
}
