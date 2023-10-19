package io.gardenerframework.fragrans.data.persistence.test.cases;

import io.gardenerframework.fragrans.data.persistence.orm.database.Database;
import io.gardenerframework.fragrans.data.persistence.orm.entity.FieldScanner;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.JsonObjectArrayColumn;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.JsonObjectColumn;
import io.gardenerframework.fragrans.data.persistence.test.DataPersistenceTestApplication;
import io.gardenerframework.fragrans.data.schema.entity.BasicEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author zhanghan30
 * @date 2022/9/24 18:26
 */
@DisplayName("json列测试")
@SpringBootTest(classes = DataPersistenceTestApplication.class)
@ActiveProfiles("mysql")
public class JsonColumnTest {
    @BeforeEach
    public void ensureDriver() {
        Database.setDriver(DatabaseDriver.MYSQL);
    }

    @Test
    public void smokeTest() {
        JsonObjectColumn hehe = new JsonObjectColumn(
                FieldScanner.getInstance().columns(
                        BasicEntity.class
                ),
                column -> FieldScanner.getInstance().getConverter(BasicEntity.class).columnToField(column),
                "hehe"
        );
        Assertions.assertEquals("JSON_OBJECT(\"id\",`id`,\"createdTime\",`created_time`,\"lastUpdateTime\",`last_update_time`) AS `hehe`", hehe.build());
        JsonObjectArrayColumn haha = new JsonObjectArrayColumn(
                FieldScanner.getInstance().columns(
                        BasicEntity.class
                ),
                column -> FieldScanner.getInstance().getConverter(BasicEntity.class).columnToField(column),
                "haha"
        );
        Assertions.assertEquals("JSON_ARRAYAGG(JSON_OBJECT(\"id\",`id`,\"createdTime\",`created_time`,\"lastUpdateTime\",`last_update_time`)) AS `haha`", haha.build());
    }
}
