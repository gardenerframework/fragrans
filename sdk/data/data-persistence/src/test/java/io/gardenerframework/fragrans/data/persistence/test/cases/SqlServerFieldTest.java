package io.gardenerframework.fragrans.data.persistence.test.cases;

import io.gardenerframework.fragrans.data.persistence.test.DataPersistenceTestApplication;
import io.gardenerframework.fragrans.data.persistence.test.utils.fieldTest.*;
import io.gardenerframework.fragrans.data.schema.query.GenericQueryResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

/**
 * @author ZhangHan
 * @date 2022/6/16 0:20
 */
@DisplayName("实际SqlServer操作数据库测试")
@SpringBootTest(classes = DataPersistenceTestApplication.class)
@MapperScan(basePackageClasses = FieldTestDao.class)
@ActiveProfiles("sql-server")
public class SqlServerFieldTest {
    @Autowired
    private FieldTestDao dao;

    @Autowired
    private FieldTestService service;
    @Autowired
    private JsonTestDao jsonTestDao;

    @Test
    public void smokeTest() {
        dao.deleteAll();
        int size = 200;
        ArrayList<FieldTestObject> objects = new ArrayList<>(size);
        //生成1000个测试对象
        List<String> ids = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            String id = UUID.randomUUID().toString();
            objects.add(new FieldTestObject(new Date(), null, id, UUID.randomUUID().toString()));
            ids.add(id);
        }
        //批量加进去
        dao.batchAdd(objects);
        Assertions.assertEquals(size, dao.count());
        //分页查询
        for (int i = 0; i < 10; i++) {
            int pageSize = size / 10;
            GenericQueryResult<FieldTestObject> query = service.query(null, i + 1, pageSize);
            Assertions.assertEquals(pageSize, query.getContents().size());
            //总数对
            Assertions.assertEquals(size, query.getTotal());
        }
        //带条件查询
        List<FieldTestObject> query = dao.query("1", 1, size);
        Assertions.assertTrue(query.size() < size);
        Assertions.assertTrue(query.size() > 0);
        //再插一个单独的数据
        String id = UUID.randomUUID().toString();
        FieldTestObject added = new FieldTestObject(new Date(), null, id, UUID.randomUUID().toString());
        added.setOther(true);
        dao.add(added);
        Assertions.assertEquals(size + 1, dao.count());
        //读取这个单独的数据
        FieldTestObject fromDatabase = dao.get(id);
        Assertions.assertEquals(added.getId(), fromDatabase.getId());
        Assertions.assertEquals(added.getTest(), fromDatabase.getTest());
        Assertions.assertTrue(fromDatabase.isOther());
        //更新这个单独的数据
        String newText = UUID.randomUUID().toString();
        dao.update(id, newText);
        fromDatabase = dao.get(id);
        Assertions.assertEquals(newText, fromDatabase.getTest());
        //覆盖更新
        FieldTestObject newObject = new FieldTestObject();
        newObject.setTest(UUID.randomUUID().toString());
        dao.updateRecord(id, newObject);
        fromDatabase = dao.get(id);
        Assertions.assertEquals(id, fromDatabase.getId());
        Assertions.assertEquals(newObject.getTest(), fromDatabase.getTest());
        Assertions.assertNotEquals(newText, fromDatabase.getTest());
        //特别操作字段已经过滤掉了
        Assertions.assertTrue(fromDatabase.isOther());
        dao.updateTwinFiled(id, newText = UUID.randomUUID().toString(), false);
        fromDatabase = dao.get(id);
        Assertions.assertEquals(newText, fromDatabase.getTest());
        Assertions.assertFalse(fromDatabase.isOther());
        //删除这个单独的数据
        dao.deleteById(id);
        Assertions.assertEquals(size, dao.count());
        Assertions.assertNull(dao.get(id));
        //尝试批量操作
        Assertions.assertEquals(size, dao.batchNestedSelect(ids).size());
        Assertions.assertEquals(size, dao.batchNestedCollectionSelect(new IdsInNestedObject(ids)).size());
    }
}
