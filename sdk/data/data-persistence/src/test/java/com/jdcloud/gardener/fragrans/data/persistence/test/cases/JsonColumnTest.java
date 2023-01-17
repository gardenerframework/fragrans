package com.jdcloud.gardener.fragrans.data.persistence.test.cases;

import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.JsonObjectArrayColumn;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.JsonObjectColumn;
import com.jdcloud.gardener.fragrans.data.persistence.test.DataPersistenceTestApplication;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhanghan30
 * @date 2022/9/24 18:26
 */
@DisplayName("json列测试")
@SpringBootTest(classes = DataPersistenceTestApplication.class)
public class JsonColumnTest {
    @Test
    public void smokeTest() {
        JsonObjectColumn hehe = new JsonObjectColumn(
                FieldScannerStaticAccessor.scanner().columns(
                        BasicEntity.class
                ),
                column -> FieldScannerStaticAccessor.scanner().getConverter(BasicEntity.class).columnToField(column),
                "hehe"
        );
        Assertions.assertEquals("JSON_OBJECT(\"id\", `id`,\"createdTime\", `created_time`,\"lastUpdateTime\", `last_update_time`) AS `hehe`", hehe.build());
        JsonObjectArrayColumn haha = new JsonObjectArrayColumn(
                FieldScannerStaticAccessor.scanner().columns(
                        BasicEntity.class
                ),
                column -> FieldScannerStaticAccessor.scanner().getConverter(BasicEntity.class).columnToField(column),
                "haha"
        );
        Assertions.assertEquals("JSON_ARRAYAGG(JSON_OBJECT(\"id\", `id`,\"createdTime\", `created_time`,\"lastUpdateTime\", `last_update_time`)) AS `haha`", haha.build());
    }
}
