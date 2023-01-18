package io.gardenerframework.fragrans.data.persistence.test.cases;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author zhanghan30
 * @date 2022/6/14 5:35 下午
 */
@DisplayName("类扫描测试")
@SpringBootTest(classes = DataPersistenceTestApplication.class)
public class FieldScannerTest {
    @Autowired
    private FieldScanner fieldScanner;

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
        Assertions.assertEquals(3, fields.size());
        Assertions.assertEquals(1, new HashSet<>(fields).size());
        Assertions.assertTrue(new HashSet<>(fields).contains("all-same"));

        fields = fieldScanner.columns(TestObjectWithConverterAnnotation.class);
        Assertions.assertEquals(3, fields.size());
        Assertions.assertEquals(1, new HashSet<>(fields).size());
        Assertions.assertTrue(new HashSet<>(fields).contains("all-same"));

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
    }

    public static class TestObject extends BasicEntity<String> {
    }

    @UsingColumnNameConverter(TestColumnNameConverter.class)
    public static class TestObjectWithConverterAnnotation extends BasicEntity<String> {

    }
}
