package com.jdcloud.gardener.fragrans.data.persistence.test.utils.fieldTest;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableName;
import com.jdcloud.gardener.fragrans.data.schema.annotation.UpdateBySpecificOperation;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author ZhangHan
 * @date 2022/6/16 0:21
 */
@TableName("data_persistence_field_test")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class FieldTestObject extends BasicEntity<String> {
    private String test;
    @UpdateBySpecificOperation
    private boolean other;

    public FieldTestObject(Date createdTime, Date lastUpdateTime, String id, String test) {
        super(createdTime, lastUpdateTime, id);
        this.test = test;
    }
}
