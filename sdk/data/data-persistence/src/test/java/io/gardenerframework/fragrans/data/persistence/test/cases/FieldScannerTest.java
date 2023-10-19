package io.gardenerframework.fragrans.data.persistence.test.cases;

import io.gardenerframework.fragrans.data.persistence.orm.database.Database;
import io.gardenerframework.fragrans.data.persistence.orm.entity.FieldScanner;
import io.gardenerframework.fragrans.data.persistence.orm.entity.annotation.UsingColumnNameConverter;
import io.gardenerframework.fragrans.data.persistence.orm.entity.converter.NoopConverter;
import io.gardenerframework.fragrans.data.persistence.test.DataPersistenceTestApplication;
import io.gardenerframework.fragrans.data.persistence.test.utils.TestColumnNameConverter;
import io.gardenerframework.fragrans.data.schema.annotation.ImmutableField;
import io.gardenerframework.fragrans.data.schema.entity.BasicEntity;
import io.gardenerframework.fragrans.data.schema.entity.BasicOperationTraceableEntity;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2022/6/14 5:35 下午
 */
@DisplayName("类扫描测试")
@SpringBootTest(classes = DataPersistenceTestApplication.class)
@ActiveProfiles("mysql")
public class FieldScannerTest {
    private final FieldScanner fieldScanner = FieldScanner.getInstance();

    @BeforeEach
    public void ensureDriver() {
        Database.setDriver(DatabaseDriver.MYSQL);
    }

    @Test
    public void smokeTest() {
        Collection<String> fields = fieldScanner.columns(TestObject.class);
        Assertions.assertEquals(3, fields.size());
        //默认转为下划线
        Assertions.assertTrue(fields.contains("last_update_time"));
        fields = fieldScanner.columns(TestObject.class, Collections.singletonList(ImmutableField.class), true);
        //更新时间不是不可变的
        Assertions.assertFalse(fields.contains("last_update_time"));
        //下面这2是不可变的
        Assertions.assertTrue(fields.containsAll(Arrays.asList("id", "created_time")));
        fields = fieldScanner.columns(TestObject.class, new TestColumnNameConverter());
        Assertions.assertEquals(1, fields.size());
        Assertions.assertTrue(fields.contains("all-same"));

        fields = fieldScanner.columns(TestObjectWithConverterAnnotation.class);
        Assertions.assertEquals(1, fields.size());
        Assertions.assertTrue(fields.contains("all-same"));

        Collection<String> scan = fieldScanner.columns(BasicOperationTraceableEntity.class, Arrays.asList(GenericTraits.IdentifierTraits.Id.class, SecurityTraits.AuditingTraits.IdentifierTraits.Updater.class));
        Assertions.assertTrue(scan.contains("id"));
        Assertions.assertTrue(scan.contains("updater"));

        String scanNotCached = fieldScanner.column(BasicEntity.class, SecurityTraits.AuditingTraits.DatetimeTraits.LastUpdateTime.class);
        String scanCached = fieldScanner.column(BasicEntity.class, SecurityTraits.AuditingTraits.DatetimeTraits.LastUpdateTime.class);
        Assertions.assertEquals(scanNotCached, scanCached);
        scanNotCached = fieldScanner.column(BasicEntity.class, new NoopConverter(), SecurityTraits.AuditingTraits.DatetimeTraits.LastUpdateTime.class);
        Assertions.assertNotEquals(scanCached, scanNotCached);
        scanCached = fieldScanner.column(BasicEntity.class, new NoopConverter(), SecurityTraits.AuditingTraits.DatetimeTraits.LastUpdateTime.class);
        Assertions.assertEquals(scanNotCached, scanCached);
        scanNotCached = fieldScanner.column(BasicEntity.class, SecurityTraits.AuditingTraits.DatetimeTraits.CreatedTime.class);
        Assertions.assertNotEquals(scanCached, scanNotCached);
        scanCached = fieldScanner.column(BasicEntity.class, SecurityTraits.AuditingTraits.DatetimeTraits.CreatedTime.class);
        Assertions.assertEquals(scanNotCached, scanCached);
        scan = fieldScanner.columns(TestObjectForBasicEntity.class);
        Assertions.assertTrue(scan.contains("id"));
        scan = fieldScanner.columns(TestObjectForBasicEntityButDifferentType.class);
        Assertions.assertTrue(scan.contains("id"));

    }

    public static class TestObject extends BasicEntity<String> {
    }

    @UsingColumnNameConverter(TestColumnNameConverter.class)
    public static class TestObjectWithConverterAnnotation extends BasicEntity<String> {

    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class TestObjectForBasicEntity extends TestObject {
        @NonNull
        private String id;
    }

    public static class TestObjectForBasicEntityButDifferentType extends TestObject {
        private int id;
    }
}
