package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement;

import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.converter.ColumnNameConverter;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BasicCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.BasicValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author zhanghan30
 * @date 2022/6/16 12:08 下午
 */
public class UpdateStatement extends BasicRecordSetModificationStatement<UpdateStatement> {
    private BasicCriteria queryCriteria;

    public UpdateStatement(ColumnNameConverter defaultConverter) {
        super(defaultConverter);
    }

    public UpdateStatement where(BasicCriteria criteria) {
        this.queryCriteria = criteria;
        return this;
    }

    @Override
    protected String buildInternally() {
        return appendQueryCriteria(
                appendValues(String.format("UPDATE %s", getTable().build())
                ), this.queryCriteria);
    }

    /**
     * 完成值和列的配对构建
     *
     * @param statement 语句
     * @return 语句
     */
    private String appendValues(String statement) {
        Iterator<Column> columnIterator = this.getColumns().iterator();
        Iterator<BasicValue> valueIterator = this.getValues().iterator();
        Collection<String> values = new ArrayList<>(this.getColumns().size());
        while (columnIterator.hasNext()) {
            Column column = columnIterator.next();
            BasicValue value = valueIterator.next();
            values.add(String.format("%s = %s", column.build(), value.build()));
        }
        return String.format("%s SET %s", statement, String.join(",", values));
    }
}
