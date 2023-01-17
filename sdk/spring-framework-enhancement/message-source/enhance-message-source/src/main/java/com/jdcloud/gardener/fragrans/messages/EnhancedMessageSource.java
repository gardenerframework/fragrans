package com.jdcloud.gardener.fragrans.messages;

import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;

import java.util.Locale;

/**
 * 经过长期的时间
 * <p>
 * 需要直接将对象转为目标消息
 * <p>
 * 对象就很多种了，比如异常(最常见)
 *
 * @author zhanghan30
 * @date 2022/5/9 4:40 下午
 */
public interface EnhancedMessageSource extends MessageSource {
    /**
     * 返回基于类名称获取的消息
     *
     * @param target         目标
     * @param defaultMessage 默认消息
     * @param locale         本地信息
     * @param <T>            没啥用
     * @return 消息
     */
    default <T> String getMessage(T target, @Nullable String defaultMessage, Locale locale) {
        return getMessage(
                target instanceof String ? (String) target :
                        target.getClass().getCanonicalName(), target instanceof MessageArgumentsSupplier ? ((MessageArgumentsSupplier) target).getMessageArguments() : null,
                defaultMessage,
                locale
        );
    }

    /**
     * 返回基于类名称获取的消息
     *
     * @param target 目标
     * @param locale 本地信息
     * @param <T>    没啥用
     * @return 消息
     */
    default <T> String getMessage(T target, Locale locale) {
        return getMessage(
                target,
                null,
                locale
        );
    }

    /**
     * 基于类型获得消息
     *
     * @param target         目标没醒
     * @param defaultMessage 默认消息
     * @param locale         本地信息
     * @return 消息
     */
    default String getMessage(Class<?> target, @Nullable String defaultMessage, Locale locale) {
        return getMessage(
                target.getCanonicalName(),
                null,
                defaultMessage,
                Locale.getDefault()
        );
    }

    /**
     * 基于类型获得消息
     *
     * @param target 目标没醒
     * @param locale 本地信息
     * @return 消息
     */
    default String getMessage(Class<?> target, Locale locale) {
        return getMessage(target, null, locale);
    }
}
