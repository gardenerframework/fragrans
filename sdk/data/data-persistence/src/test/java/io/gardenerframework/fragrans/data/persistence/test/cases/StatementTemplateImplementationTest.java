package io.gardenerframework.fragrans.data.persistence.test.cases;

import io.gardenerframework.fragrans.data.persistence.test.DataPersistenceTestApplication;
import io.gardenerframework.fragrans.data.persistence.test.utils.mapper.IntTemplate;
import io.gardenerframework.fragrans.data.persistence.test.utils.mapper.StringTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ZhangHan
 * @date 2022/6/16 0:20
 */
@DisplayName("StatementProvider测试")
@SpringBootTest(classes = DataPersistenceTestApplication.class)
@MapperScan(basePackageClasses = IntTemplate.class)
public class StatementTemplateImplementationTest {
    @Autowired
    private IntTemplate intTemplate;

    @Autowired
    private StringTemplate stringTemplate;

    @Test
    public void smokeTest() {
        Assertions.assertEquals(1, intTemplate.select(""));
        Assertions.assertEquals(String.class.getName(), stringTemplate.select(""));
    }
}
