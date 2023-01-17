package com.jdcloud.gardener.fragrans.data.cache.log.schema.detail;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import org.springframework.lang.Nullable;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * @author ZhangHan
 * @date 2022/6/17 23:14
 */
public class CacheDetail implements Detail {
    private String key;
    private String until;

    public CacheDetail(String key, @Nullable Duration ttl) {
        this.key = key;
        this.until = (ttl == null ? null : new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601).format(Date.from(Instant.now().plus(ttl))));
    }
}
