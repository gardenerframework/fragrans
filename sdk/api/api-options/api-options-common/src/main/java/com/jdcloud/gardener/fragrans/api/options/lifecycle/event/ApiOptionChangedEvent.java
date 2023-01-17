package com.jdcloud.gardener.fragrans.api.options.lifecycle.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * 选项变更事件，由接口触发或外部消息广播通道
 * <p>
 * 当用于广播时，可以直接序列化和反序列化这个事件
 *
 * @author zhanghan30
 * @date 2022/5/10 4:19 上午
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ApiOptionChangedEvent implements Serializable {
    /**
     * 对应的选项id
     */
    private String id;
    /**
     * 变更后的预览
     * <p>
     * 如果是来自其它实例的变更通告，则回事null
     * <p>
     * 如果是从接口变更来的，则是变更或的值
     */
    @Nullable
    private transient Object option;
    /**
     * 变更的操作方的实例id
     * <p>
     * 当事件变更的操作方与发送方相同时不会触发选项的重新加载
     */
    private String instanceId;
    /**
     * 事件来源
     */
    private Source source;

    public enum Source {
        /**
         * 由当前实例接口进行的变更
         */
        ENDPOINT,
        /**
         * 由外部消息队列或其它机制受到的变更
         */
        NOTIFICATION;
    }
}