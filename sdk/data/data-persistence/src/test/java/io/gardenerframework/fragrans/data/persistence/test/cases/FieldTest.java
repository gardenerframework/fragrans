package io.gardenerframework.fragrans.data.persistence.test.cases;

import io.gardenerframework.fragrans.data.persistence.test.DataPersistenceTestApplication;
import io.gardenerframework.fragrans.data.persistence.test.utils.fieldTest.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author ZhangHan
 * @date 2022/6/16 0:20
 */
@DisplayName("实际操作数据库测试")
@SpringBootTest(classes = DataPersistenceTestApplication.class)
@MapperScan(basePackageClasses = FieldTestDao.class)
public class FieldTest {
    @Autowired
    private FieldTestDao dao;
    @Autowired
    private JsonTestDao jsonTestDao;

    @Test
    public void smokeTest() {
        dao.deleteAll();
        ArrayList<FieldTestObject> objects = new ArrayList<>(1000);
        //生成1000个测试对象
        List<String> ids = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            String id = UUID.randomUUID().toString();
            objects.add(new FieldTestObject(new Date(), null, id, UUID.randomUUID().toString()));
            ids.add(id);
        }
        //批量加进去
        dao.batchAdd(objects);
        Assertions.assertEquals(1000, dao.count());
        //分页查询
        for (int i = 0; i < 10; i++) {
            List<FieldTestObject> page = dao.query(null, i + 1, 100);
            //数量对
            Assertions.assertEquals(100, page.size());
            //总数对
            Assertions.assertEquals(1000, dao.foundRows());
        }
        //带条件查询
        List<FieldTestObject> query = dao.query("1", 1, 1000);
        Assertions.assertTrue(query.size() < 1000);
        Assertions.assertTrue(query.size() > 0);
        //再插一个单独的数据
        String id = UUID.randomUUID().toString();
        FieldTestObject added = new FieldTestObject(new Date(), null, id, UUID.randomUUID().toString());
        added.setOther(true);
        dao.add(added);
        Assertions.assertEquals(1001, dao.count());
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
        Assertions.assertEquals(1000, dao.count());
        Assertions.assertNull(dao.get(id));
        //尝试批量操作
        Assertions.assertEquals(1000, dao.batchNestedSelect(ids).size());
        Assertions.assertEquals(1000, dao.batchNestedCollectionSelect(new IdsInNestedObject(ids)).size());
    }

    @Test
    public void jsonHandlerTest() {
        jsonTestDao.delete();
        Map<String, Object> map = new HashMap();
        map.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        jsonTestDao.create(new JsonTestObject(
                Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                Collections.singletonList(new JsonTestObject.Nested(UUID.randomUUID().toString())),
                map,
                new JsonTestObject.Nested(UUID.randomUUID().toString())
        ));
        Collection<JsonTestObject> read = jsonTestDao.read();
        Assertions.assertNotNull(CollectionUtils.firstElement(new HashSet<>(read)).getPrimitive());
        Assertions.assertNotNull(CollectionUtils.firstElement(new HashSet<>(read)).getList());
        Assertions.assertTrue(
                CollectionUtils.firstElement(new ArrayList<>(CollectionUtils.firstElement(new HashSet<>(read)).getList()))
                        instanceof JsonTestObject.Nested
        );
        Assertions.assertEquals(map, CollectionUtils.firstElement(new HashSet<>(read)).getMap());
    }
}
