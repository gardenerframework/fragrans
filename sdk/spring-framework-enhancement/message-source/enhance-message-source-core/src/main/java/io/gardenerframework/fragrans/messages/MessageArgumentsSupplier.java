package io.gardenerframework.fragrans.messages;


/**
 * 相关的实现类能够向消息提供参数数据
 *
 * @author zhanghan30
 * @date 2022/5/9 4:40 下午
 */
@FunctionalInterface
public interface MessageArgumentsSupplier {
    /**
     * 获得消息参数数组
     *
     * @return 消息参数数组
     */
    Object[] getMessageArguments();
}
