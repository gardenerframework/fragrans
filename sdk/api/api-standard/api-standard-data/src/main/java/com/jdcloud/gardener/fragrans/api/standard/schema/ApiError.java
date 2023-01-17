package com.jdcloud.gardener.fragrans.api.standard.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.Map;

/**
 * 标准的api错误输出结构定义<br>
 * 这个类主要用于接口之间横向调用时反序列化错误信息时使用<br>
 *
 * @author zhanghan
 * @date 2020-11-07 12:06
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    /*请求上下文相关*/
    /**
     * 错误发生的时间戳，
     * 按照接口开发规范的要求，应当符合iso-8601格式
     * 自spring boot 2.5.2开始，默认会使用以上标准格式来格式化{@link Date}类型的数据
     * 之前是用整数
     */
    private Date timestamp;
    /**
     * 请求返回的http状态码，一般为4xx或5xx
     */
    private int status;
    /**
     * 和{@link #status}字段的状态码对应的http状态短语
     */
    private String reason;
    /**
     * 请求地址
     * <p>
     * 注意，该地址也许和从网关上看到的调用地址不一样，因为网关可能会添加一些前缀。
     * 这个地址一般是后台服务对应的真实地址
     */
    private String uri;
    /* 错误相关 */
    /**
     * 错误的唯一识别符号
     * <p>
     * 调用方可基于这个错误识别符号获取到问题发生的原因
     */
    private String error;
    /**
     * 错误信息文本
     */
    private String message;
    /*处理手段相关*/
    /**
     * 错误提示
     * <p>
     * 这是一个扩展字段，目前还没有正式启用
     */
    private String hint;
    /**
     * 错误的详细信息
     */
    private Map<String, Object> details;
}
