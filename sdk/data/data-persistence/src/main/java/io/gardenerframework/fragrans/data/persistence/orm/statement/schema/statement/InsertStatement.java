package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement;

import io.gardenerframework.fragrans.data.persistence.orm.entity.converter.ColumnNameConverter;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.BasicElement;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.Column;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.BasicValue;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.FieldNameValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/6/15 4:12 下午
 */

public class InsertStatement extends BasicRecordSetModificationStatement<InsertStatement> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private boolean batchMode;
    private String collection;
    private String item;

    public InsertStatement(ColumnNameConverter defaultConverter) {
        super(defaultConverter);
    }

    /**
     * 准备插入的属性列
     *
     * @param column 列名，自动加重音
     * @return 语句
     */
    @Override
    public InsertStatement column(String column, Function<String, BasicValue> columnValueMapper) {
        if (isBatchMode()) {
            throw new IllegalStateException("batch mode enabled.");
        }
        return super.column(column, columnValueMapper);
    }

    /**
     * 给一个批量插入的语句
     * <p>
     * 一旦开启批量模式，就只能重复覆盖列和值清单
     *
     * @param columns    属性列
     * @param collection 集合名称
     * @param item       单个元素名称
     * @return 语句
     */
    public InsertStatement batch(Collection<String> columns, String collection, String item) {
        return batch(columns, collection, item, (ColumnNameConverter) null);
    }

    /**
     * 给一个批量插入的语句
     * <p>
     * 一旦开启批量模式，就只能重复覆盖列和值清单
     *
     * @param columns             属性列
     * @param collection          集合名称
     * @param item                单个元素名称
     * @param columnNameConverter 列到值的映射器
     * @return 语句
     */
    public InsertStatement batch(Collection<String> columns, String collection, String item, @Nullable ColumnNameConverter columnNameConverter) {
        return batch(columns, collection, item, column -> columnNameConverter == null ? getDefaultConverter().columnToField(column) : columnNameConverter.columnToField(column));
    }

    /**
     * 给一个批量插入的语句
     * <p>
     * 一旦开启批量模式，就只能重复覆盖列和值清单
     *
     * @param columns             属性列
     * @param collection          集合名称
     * @param item                单个元素名称
     * @param columnToFieldMapper 列到值的映射器
     * @return 语句
     */
    public InsertStatement batch(Collection<String> columns, String collection, String item, Function<String, String> columnToFieldMapper) {
        return batch(columns, collection, item, (itemName, columnName) -> new FieldNameValue(itemName, columnToFieldMapper.apply(columnName)));
    }

    /**
     * 给一个批量插入的语句
     * <p>
     * 一旦开启批量模式，就只能重复覆盖列和值清单
     *
     * @param columns           属性列
     * @param collection        集合名称
     * @param item              单个元素名称
     * @param columnValueMapper 数据库列到实体字段的映射关系
     * @return 语句
     */
    public InsertStatement batch(Collection<String> columns, String collection, String item, BiFunction<String, String, BasicValue> columnValueMapper) {
        setBatchMode(true);
        this.getColumns().clear();
        this.getValues().clear();
        this.collection = collection;
        this.item = item;
        columns.forEach(
                column -> {
                    this.getColumns().add(new Column(column));
                    this.getValues().add(columnValueMapper.apply(item, column));
                }
        );
        return this;
    }

    @Override
    protected String buildInternally() {
        String batchStart = String.format("<foreach item=\"%s\" collection=\"%s\" separator=\",\">%n", item, collection);
        String batchEnd = "</foreach>";
        return String.format("INSERT INTO %s (%s) VALUES %s(%s)%n%s",
                getTable().build(),
                this.getColumns().stream().map(Column::build).collect(Collectors.joining(",")),
                isBatchMode() ? batchStart : "",
                this.getValues().stream().map(BasicElement::build).collect(Collectors.joining(",")),
                isBatchMode() ? batchEnd : ""
        );
    }
}
