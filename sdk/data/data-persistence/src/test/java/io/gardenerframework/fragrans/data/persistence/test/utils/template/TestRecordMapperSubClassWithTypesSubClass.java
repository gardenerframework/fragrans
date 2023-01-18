package io.gardenerframework.fragrans.data.persistence.test.utils.template;

import org.springframework.context.annotation.Primary;

/**
 * @author ZhangHan
 * @date 2022/11/1 17:26
 */
@Primary
public interface TestRecordMapperSubClassWithTypesSubClass extends TestRecordMapperSubClassWithTypes<SecondTemplate> {
}
