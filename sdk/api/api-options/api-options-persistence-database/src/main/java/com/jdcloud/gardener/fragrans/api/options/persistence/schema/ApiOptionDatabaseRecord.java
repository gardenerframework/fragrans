package com.jdcloud.gardener.fragrans.api.options.persistence.schema;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableName;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicOperationTraceableEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/7/7 4:00 下午
 */
@TableName("api_option")
@Getter
@Setter
public class ApiOptionDatabaseRecord extends BasicOperationTraceableEntity<String> implements ApiOptionRecordSkeleton {
    private Map<String, Object> option;
    private String versionNumber;
}
