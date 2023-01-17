package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement;

import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.converter.ColumnNameConverter;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * 基本的记录集创建和更新语句
 * <p>
 * 对应的就是插入语句和更新语句
 *
 * @author zhanghan30
 * @date 2022/6/16 12:14 下午
 */
@AllArgsConstructor
public abstract class BasicRecordSetModificationStatement<S extends BasicStatement<S>> extends BasicStatement<S> {
    @Getter(AccessLevel.PROTECTED)
    private final ColumnNameConverter defaultConverter;
    //要操作的列
    @Getter(AccessLevel.PROTECTED)
    private final Collection<Column> columns = new LinkedList<>();
    @Getter(AccessLevel.PROTECTED)
    private final Collection<BasicValue> values = new LinkedList<>();

    /**
     * 支持进行简单的属性列与值之间的映射对构建
     * <p>
     * 参数输入一个列的清单，并给一个参数名，相关的对应关系就变成：列名 <-> #{参数名.默认转换器转换的属性名}的模式
     *
     * @param columns    列清单，自动加重音符号
     * @param entityName 实体名称，一般来说就是mybatis的参数名
     * @return 语句
     */
    public S columns(Collection<String> columns, String entityName) {
        return columns(columns, entityName, (ColumnNameConverter) null);
    }

    /**
     * 支持进行简单的属性列与值之间的映射对构建
     * <p>
     * 参数输入一个列名称，并给一个参数名，相关的对应关系就变成：列名 <-> #{参数名.默认转换器转换的属性名}的模式
     *
     * @param column     列名，自动加重音
     * @param entityName 实体名称，一般来说就是mybatis的参数名
     * @return 语句
     */
    public S column(String column, String entityName) {
        return column(column, entityName, (ColumnNameConverter) null);
    }

    /**
     * 支持进行简单的属性列与值之间的映射对构建
     * <p>
     * 参数输入一个列名称的清单，并给一个参数名，相关的对应关系就变成：列名 <-> #{参数名.默认转换器转换的属性名}的模式
     * <p>
     * 如果给定的转换器为null，则使用语句实例中设置的默认转换器
     *
     * @param columns             列清单，自动加重音符号
     * @param entityName          实体名称，一般来说就是mybatis的参数名
     * @param columnNameConverter 数据库列到实体字段的映射关系
     * @return 语句
     */
    public S columns(Collection<String> columns, String entityName, @Nullable ColumnNameConverter columnNameConverter) {
        return columns(columns, s -> new FieldNameValue(
                        entityName,
                        columnNameConverter == null ? defaultConverter.columnToField(s) :
                                columnNameConverter.columnToField(s)
                )
        );
    }

    /**
     * 支持进行简单的属性列与值之间的映射对构建
     * <p>
     * 参数输入一个列名称，并给一个参数名，相关的对应关系就变成：列名 <-> #{参数名.默认转换器转换的属性名}的模式
     * <p>
     * 如果给定的转换器为null，则使用语句实例中设置的默认转换器
     *
     * @param column              列名，自动加重音
     * @param entityName          实体名称，一般来说就是mybatis的参数名
     * @param columnNameConverter 数据库列到实体字段的映射关系
     * @return 语句
     */
    public S column(String column, String entityName, @Nullable ColumnNameConverter columnNameConverter) {
        return column(column, s -> new FieldNameValue(
                        entityName,
                        columnNameConverter == null ? defaultConverter.columnToField(s) :
                                columnNameConverter.columnToField(s)
                )
        );
    }

    /**
     * 支持进行简单的属性列与值之间的映射对构建
     * <p>
     * 参数输入一个列名称的清单，并给一个参数名，相关的对应关系就变成：列名 <-> #{参数名.映射器转换的属性名}的模式
     *
     * @param columns             列清单，自动加重音符号
     * @param entityName          实体名称，一般来说就是mybatis的参数名
     * @param columnToFieldMapper 数据库列到实体字段的映射关系
     * @return 语句
     */
    public S columns(Collection<String> columns, String entityName, Function<String, String> columnToFieldMapper) {
        return columns(columns, s -> new FieldNameValue(entityName, columnToFieldMapper.apply(s)));
    }

    /**
     * 支持进行简单的属性列与值之间的映射对构建
     * <p>
     * 参数输入一个列名称的清单，并给一个参数名，相关的对应关系就变成：列名 <-> #{参数名.映射器转换的属性名}的模式
     *
     * @param column              列名，自动加重音
     * @param entityName          实体名称，一般来说就是mybatis的参数名
     * @param columnToFieldMapper 数据库列到实体字段的映射关系
     * @return 语句
     */
    public S column(String column, String entityName, Function<String, String> columnToFieldMapper) {
        return column(column, s -> new FieldNameValue(entityName, columnToFieldMapper.apply(s)));
    }

    /**
     * 最原始的操作方法
     * <p>
     * 给定一个列的清单，再给一个每一列应当如何转为值的映射器
     *
     * @param columns 列清单，自动加重音符号
     * @return 语句
     * @see FieldNameValue
     * @see ParameterNameValue
     * @see TextValue
     * @see RawValue
     */
    @SuppressWarnings("unchecked")
    public S columns(Collection<String> columns, Function<String, BasicValue> columnValueMapper) {
        columns.forEach(
                column -> this.column(column, columnValueMapper)
        );
        return (S) this;
    }

    /**
     * 最原始的操作方法
     * <p>
     * 给定一个列的名称，再给一个每一列应当如何转为值的映射器
     *
     * @param column 列名，自动加重音
     * @return 语句
     */
    @SuppressWarnings("unchecked")
    public S column(String column, Function<String, BasicValue> columnValueMapper) {
        this.columns.add(new Column(column));
        this.values.add(columnValueMapper.apply(column));
        return (S) this;
    }
}
