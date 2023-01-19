package io.gardenerframework.fragrans.api.standard.error.configuration;

import org.springframework.util.ClassUtils;

/**
 * @author zhanghan30
 * @date 2022/8/26 8:48 上午
 */
public interface ApiErrorRegistry {
    /**
     * 当前错误是否是应当暴露的
     *
     * @param error 错误
     * @return true - 应当暴露 / false - 应当隐藏
     */
    default boolean isErrorRevealed(Object error) {
        return isErrorRevealed(ClassUtils.getUserClass(error));
    }

    /**
     * 当前错误是否是应当暴露的
     *
     * @param clazz 错误类
     * @return true - 应当暴露 / false - 应当隐藏
     */
    boolean isErrorRevealed(Class<?> clazz);
}
