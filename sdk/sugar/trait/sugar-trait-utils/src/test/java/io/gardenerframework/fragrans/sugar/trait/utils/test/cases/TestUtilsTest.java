package io.gardenerframework.fragrans.sugar.trait.utils.test.cases;

import io.gardenerframework.fragrans.sugar.trait.utils.TraitUtils;
import io.gardenerframework.fragrans.sugar.trait.utils.test.TraitUtilsTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/11/28 15:51
 */
@SpringBootTest(classes = TraitUtilsTestApplication.class)
public class TestUtilsTest {
    @Test
    public void smokeTest() {
        Assertions.assertTrue(TraitUtils.isTrait(Trait.class));
        Assertions.assertTrue(TraitUtils.isSingleFieldTrait(Trait.class));
        Collection<String> traitFields = TraitUtils.getTraitFieldNames(Trait.class);
        Assertions.assertEquals(1, traitFields.size());
        Assertions.assertTrue(traitFields.contains("some"));
        Assertions.assertFalse(TraitUtils.isTrait(NonTrait.class));
    }

    public interface Trait {
        String getSome();

        void setSome(String some);
    }

    public interface NonTrait {
        String getSome();

        //setter有返回值
        String setSome(String some);
    }
}
