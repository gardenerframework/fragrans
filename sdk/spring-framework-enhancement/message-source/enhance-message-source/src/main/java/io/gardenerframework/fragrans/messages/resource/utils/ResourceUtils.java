package io.gardenerframework.fragrans.messages.resource.utils;

/**
 * @author ZhangHan
 * @date 2022/6/10 0:38
 */
public abstract class ResourceUtils {
    private ResourceUtils() {

    }

    /**
     * 将资源包的名称和后缀拼接在一起
     *
     * @param bundleName 资源名称
     * @param suffix     后缀
     * @return 路径
     */
    public static String toResourceName(String bundleName, String suffix) {
        return bundleName.replace('.', '/') + '.' + suffix;
    }
}
