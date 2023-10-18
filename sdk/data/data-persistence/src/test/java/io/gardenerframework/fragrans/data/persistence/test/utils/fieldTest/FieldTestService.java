package io.gardenerframework.fragrans.data.persistence.test.utils.fieldTest;

import io.gardenerframework.fragrans.data.persistence.configuration.DataPersistenceComponent;
import io.gardenerframework.fragrans.data.schema.query.GenericQueryResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FieldTestService {
    public FieldTestDao dao;

    public GenericQueryResult<FieldTestObject> query(String prefix, int pageNo, int pageSize) {
        return GenericQueryResult.<FieldTestObject>builder()
                .contents(dao.query(prefix, pageNo, pageSize))
                .total(dao.foundRows(prefix))
                .build();
    }
}
