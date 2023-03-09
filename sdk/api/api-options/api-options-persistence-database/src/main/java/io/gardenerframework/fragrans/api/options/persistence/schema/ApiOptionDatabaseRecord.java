package io.gardenerframework.fragrans.api.options.persistence.schema;

import io.gardenerframework.fragrans.data.persistence.orm.statement.annotation.TableName;
import io.gardenerframework.fragrans.data.schema.entity.BasicOperationTraceableEntity;
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
