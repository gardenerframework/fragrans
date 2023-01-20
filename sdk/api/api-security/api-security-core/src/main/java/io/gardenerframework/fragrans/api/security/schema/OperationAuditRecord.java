package io.gardenerframework.fragrans.api.security.schema;

import io.gardenerframework.fragrans.api.security.schema.trait.ApiSecurityTraits;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import io.gardenerframework.fragrans.data.trait.network.NetworkTraits;
import io.gardenerframework.fragrans.data.trait.society.SocietyTraits;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class OperationAuditRecord implements
        GenericTraits.DatetimeTraits.Timestamp,
        ApiSecurityTraits.OperatorTraits.User,
        ApiSecurityTraits.OperatorTraits.Client,
        ApiSecurityTraits.AuditTraits.DeviceId,
        NetworkTraits.TcpIpTraits.IpAddress,
        NetworkTraits.HttpTraits.Method,
        NetworkTraits.UrlTraits.Uri,
        NetworkTraits.UrlTraits.QueryString,
        NetworkTraits.HttpTraits.Headers,
        NetworkTraits.HttpTraits.RequestBody,
        NetworkTraits.HttpTraits.Status,
        NetworkTraits.HttpTraits.ResponseBody,
        GenericTraits.IdentifierTraits.Code<String>,
        GenericTraits.StatusTraits.SuccessFlag,
        ApiSecurityTraits.AuditTraits.Snapshot,
        ApiSecurityTraits.AuditTraits.Context {
    /**
     * 时间戳
     */
    @Builder.Default
    private Date timestamp = new Date();
    /**
     * 操作用户
     */
    @Nullable
    private String userId;
    /**
     * 操作客户端
     */
    @Nullable
    private String clientId;
    /**
     * 操作设备
     */
    @Nullable
    private String deviceId;
    /**
     * ip地址
     */
    @NonNull
    private String ip;
    /**
     * 地理位置
     */
    @Nullable
    private Geolocation geolocation;

    /**
     * http方法
     */
    @NonNull
    private String method;

    /**
     * uri
     */
    @NonNull
    private String uri;

    /**
     * queryString
     */
    @Nullable
    private String queryString;
    /**
     * http头
     */
    @Singular
    @NonNull
    private Map<String, Collection<String>> headers;
    /**
     * 请求体
     */
    @Nullable
    private String requestBody;
    /**
     * http状态码
     */
    @Builder.Default
    private int status = 0;
    /**
     * 响应体
     */
    @Nullable
    private String responseBody;
    /**
     * 业务编码
     */
    @Nullable
    private String code;
    /**
     * 是否成功的标记
     */
    @Builder.Default
    private boolean success = true;
    /**
     * 变更前快照
     */
    @Nullable
    private String snapshot;
    /**
     * 其它上下文
     */
    @Nullable
    private String context;


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
        @NonNull
        private String country;
        /**
         * 省份(州)
         */
        @NonNull
        private String province;
        /**
         * 城市
         */
        @Nullable
        private String city;
    }
}
