package com.jdcloud.gardener.fragrans.api.idempotent.log.payload;

import com.jdcloud.gardener.fragrans.log.schema.payload.Payload;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpMethod;

/**
 * @author zhanghan30
 * @date 2022/2/24 2:58 下午
 */
@AllArgsConstructor
@Data
public class HttpRequestPayload implements Payload {
    private final HttpMethod method;
    private final String uri;

}
