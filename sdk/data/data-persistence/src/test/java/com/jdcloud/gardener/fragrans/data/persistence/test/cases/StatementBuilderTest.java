package com.jdcloud.gardener.fragrans.data.persistence.test.cases;

import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScanner;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.StatementBuilder;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableName;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.*;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.DeleteStatement;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.InsertStatement;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import com.jdcloud.gardener.fragrans.data.persistence.test.DataPersistenceTestApplication;
import com.jdcloud.gardener.fragrans.data.schema.annotation.DatabaseControlledField;
import com.jdcloud.gardener.fragrans.data.schema.common.BasicRecord;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2022/6/14 6:01 下午
 */
@DisplayName("语句创建测试")
@SpringBootTest(classes = DataPersistenceTestApplication.class)
public class StatementBuilderTest {
    @Autowired
    private FieldScanner scanner;
    @Autowired
    private StatementBuilder builder;

    @Test
    @DisplayName("所有必须满足测试")
    public void allCriteriaTest() {
        MatchAllCriteria criteria = new MatchAllCriteria(new EqualsCriteria("1", "1"));
        criteria.and(new EqualsCriteria("2", "2"));
        criteria.build();
    }

    @Test
    @DisplayName("bool条件测试")
    public void booleanCriteriaTest() {
        BooleanCriteria criteria = new BooleanCriteria().a(new BasicCriteria() {
            @Override
            public String build() {
                return "1 = 1";
            }
        }).and().b(new BasicCriteria() {
            @Override
            public String build() {
                return "2 = 2";
            }
        });
        BooleanCriteria parent = new BooleanCriteria().a(criteria).or().b(criteria);
        parent.build();
        Assertions.assertEquals("((1 = 1) AND (2 = 2)) OR ((1 = 1) AND (2 = 2))", parent.build());
    }

    @Test
    public void deleteStatementTest() {
        DeleteStatement statement = new DeleteStatement().table("test").where(new RawCriteria("1=1"));
        Assertions.assertEquals("<script>DELETE FROM `test` WHERE (1=1)</script>".replace(String.format("%n"), ""), statement.build().replace(String.format("%n"), ""));
        statement = new DeleteStatement().table("test").where(new BatchCriteria().collection("idList").item("id").criteria(new RawCriteria("`id`=#{id}")));
        Assertions.assertEquals("<script>DELETE FROM `test` WHERE ((<foreach item=\"id\" collection=\"idList\" separator=\"OR\">" +
                "       (`id`=#{id})" +
                "</foreach>" +
                "))</script>".replace(String.format("%n"), ""), statement.build().replace(String.format("%n"), ""));
    }

    @Test
    public void selectStatementTest() {
        SelectStatement statement = new SelectStatement().columns(scanner.columns(BasicEntity.class)).table("test").where(new RawCriteria("1=1"));
        Assertions.assertEquals("<script>SELECT `id`,`created_time`,`last_update_time` FROM `test` WHERE (1=1)</script>".replace(String.format("%n"), ""), statement.build().replace(String.format("%n"), ""));
        statement.groupBy(Collections.singleton("id")).having(new RawCriteria("1=1"));
        Assertions.assertEquals("<script>SELECT `id`,`created_time`,`last_update_time` FROM `test` WHERE (1=1) GROUP BY `id` HAVING 1=1</script>".replace(String.format("%n"), ""), statement.build().replace(String.format("%n"), ""));
        statement.orderBy("id", SelectStatement.Order.DESC);
        statement.limit(10, 10);
        statement = new SelectStatement().column("count(`id`)", false, "id_num").table("test").where(new RawCriteria("`id` >= 10"));
        statement.columns(scanner.columns(BasicRecord.class));
        statement.build();
        statement = builder.select(TestSelectObject.class, FieldScanner::columns);
        Assertions.assertEquals("<script>SELECT `id`,`created_time`,`last_update_time` FROM `select`</script>".replace(String.format("%n"), ""), statement.build().replace(String.format("%n"), ""));
        statement = builder.select(TestSelectObject.class, (scanner, clazz) -> scanner.columns(clazz, Collections.singletonList(DatabaseControlledField.class), false));
        Assertions.assertEquals("<script>SELECT `id` FROM `select`</script>".replace(String.format("%n"), ""), statement.build().replace(String.format("%n"), ""));
        //测试带表名
        statement = new SelectStatement().columns("test", Collections.singletonList("test")).table("hehe");
        Assertions.assertEquals("<script>SELECT `test`.`test` FROM `hehe`</script>".replace(String.format("%n"),
                ""), statement.build().replace(String.format("%n"), ""));
        statement = new SelectStatement().column("test", "test").table("hehe");
        Assertions.assertEquals("<script>SELECT `test`.`test` FROM `hehe`</script>".replace(String.format("%n"),
                ""), statement.build().replace(String.format("%n"), ""));
        //测试使用语句作为表名
        statement = new SelectStatement().column("test", "test").table(
                new SelectStatement().table("haha").column("hehe"),
                "t"
        );
        Assertions.assertEquals("<script>" +
                "SELECT `test`.`test` FROM ((" +
                "SELECT `hehe` FROM `haha`" +
                ") `t`)" +
                "</script>".replace(String.format("%n"), ""), statement.build().replace(String.format("%n"), ""));
        //测试join语句
        statement = new SelectStatement().column("test", "test").table("hehe")
                .join(new SelectStatement().table("haha").column("xixi"), "t")
                .on(new EqualsCriteria(new Column("hehe", "id"), new Column("xixi", "id")));
    }

    @Test
    public void insertStatementTest() {
        InsertStatement statement = builder.insert(TestInsertObject.class, (scanner, clazz) -> scanner.columns(clazz, Collections.singletonList(DatabaseControlledField.class), false), "single");
        statement = builder.insert(TestInsertObject.class, FieldScanner::columns, "list", "item");
    }

    @TableName("select")
    public static class TestSelectObject extends BasicEntity<String> {

    }

    @TableName("insert")
    public static class TestInsertObject extends BasicEntity<String> {

    }
}