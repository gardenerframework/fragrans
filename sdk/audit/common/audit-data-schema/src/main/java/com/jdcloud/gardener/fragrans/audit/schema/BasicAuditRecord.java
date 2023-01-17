package com.jdcloud.gardener.fragrans.audit.schema;

import com.jdcloud.gardener.fragrans.audit.schema.trait.*;
import com.jdcloud.gardener.fragrans.data.trait.application.ApplicationTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.data.trait.network.NetworkTraits;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import com.jdcloud.gardener.fragrans.data.trait.society.SocietyTraits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 最基本的审计记录
 *
 * @author zhanghan30
 * @date 2023/1/6 18:14
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class BasicAuditRecord<A, R, S, C> implements
        GenericTraits.IdentifierTraits.Id<String>,
        GenericTraits.DatetimeTraits.Timestamp,
        SecurityTraits.AuditingTraits.IdentifierTraits.Operator,
        DeviceId,
        ApplicationTraits.IdentifierTraits.ApplicationRelation<String>,
        NetworkTraits.TcpIpTraits.IpAddress,
        Target,
        Action,
        GenericTraits.StatusTraits.SuccessFlag,
        Arguments<A>,
        Response<R>,
        Snapshot<S>,
        Context<C> {
    /**
     * 审计记录的id
     */
    private String id;
    /**
     * 记录产生的时间
     */
    private Date timestamp;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 应用程序id(其实可能并不是真正的id，而是认知上能够识别应用程序的东西)
     */
    private String applicationId;
    /**
     * 使用的ip地址
     */
    private String ip;
    /**
     * 地址位置
     */
    private Geolocation geolocation;
    /**
     * 被操作的目标
     */
    private String target;
    /**
     * 操作的动作
     */
    private String action;
    /**
     * 操作是否成功的标志位
     */
    private boolean success;
    /**
     * 入参，是个范型
     */
    private A arguments;
    /**
     * 调用执行了什么反馈
     */
    private R response;
    /**
     * 变更操作执行前数据快照
     */
    private S snapshot;
    /**
     * 其它上下文信息
     */
    private C context;

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class Geolocation implements
            SocietyTraits.AdministrativeTraits.GroupingTraits.Country,
            SocietyTraits.AdministrativeTraits.GroupingTraits.Province,
            SocietyTraits.AdministrativeTraits.GroupingTraits.City {
        /**
         * 国家
         */
        private String country;
        /**
         * 省份(州)
         */
        private String province;
        /**
         * 城市
         */
        private String city;
    }
}
