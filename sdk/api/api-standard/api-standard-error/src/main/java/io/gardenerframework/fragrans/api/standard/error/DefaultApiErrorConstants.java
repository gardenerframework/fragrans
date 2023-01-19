package io.gardenerframework.fragrans.api.standard.error;

/**
 * 提供默认的错误码和错误状态
 *
 * @author zhanghan
 * @date 2020/11/9 16:28
 * @since 1.0.0
 */
public class DefaultApiErrorConstants {
    /**
     * 当无法提供任何错误编码时的默认的错误码，即'未知错误'
     */
    public static final String UNKNOWN_ERROR = DefaultApiErrorConstants.class.getCanonicalName() + ".unknown";
    /**
     * 一般性错误，用于掩盖错误的细节
     */
    public static final String GENERIC_ERROR = DefaultApiErrorConstants.class.getCanonicalName() + ".generic";

    /**
     * 私有构造函数，防止被错误的构造
     */
    private DefaultApiErrorConstants() {
    }
}
