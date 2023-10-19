package io.gardenerframework.fragrans.data.persistence.orm.statement;

import io.gardenerframework.fragrans.data.persistence.orm.entity.FieldScanner;
import io.gardenerframework.fragrans.data.persistence.orm.statement.annotation.TableNameUtils;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement.DeleteStatement;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement.InsertStatement;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement.UpdateStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * 创建常用的sql语句
 *
 * @author zhanghan30
 * @date 2022/6/14 5:53 下午
 */
@AllArgsConstructor
public class StatementBuilder {
    /**
     * 与属性扫描共用默认单例对象
     */
    @Getter
    private final FieldScanner fieldScanner = FieldScanner.getInstance();
    /**
     * 静态单例对象
     */
    @Getter
    private static final StatementBuilder instance = new StatementBuilder();

    /**
     * 给一个select 语句
     *
     * @return 语句
     */
    public SelectStatement select() {
        return new SelectStatement();
    }

    /**
     * 用类型快速生成一个语句
     *
     * @param clazz           类型
     * @param scannerCallback 字段扫描回调
     * @return 语句
     */
    public SelectStatement select(Class<?> clazz, BiFunction<FieldScanner, Class<?>, Collection<String>> scannerCallback) {
        return select(clazz, scannerCallback, false);
    }

    /**
     * 用类型快速生成一个语句
     *
     * @param clazz                    类型
     * @param tableNameAsRecordSetName 是否自动加表名(来自类扫描)
     * @param scannerCallback          字段扫描回调
     * @return 语句
     */
    public SelectStatement select(Class<?> clazz, BiFunction<FieldScanner, Class<?>, Collection<String>> scannerCallback, boolean tableNameAsRecordSetName) {
        return select(clazz, scannerCallback, tableNameAsRecordSetName ? TableNameUtils.getTableName(clazz) : null);
    }

    /**
     * 用类型快速生成一个语句
     *
     * @param clazz           类型
     * @param scannerCallback 字段扫描回调
     * @param recordSetName   recordSetName 记录集名称，用于多语句关联查询时使用
     * @return 语句
     */
    public SelectStatement select(Class<?> clazz, BiFunction<FieldScanner, Class<?>, Collection<String>> scannerCallback, @Nullable String recordSetName) {
        return new SelectStatement().columns(recordSetName,
                scannerCallback.apply(fieldScanner, clazz)).table(clazz);
    }

    /**
     * 给一个insert 语句
     *
     * @return 语句
     */
    public InsertStatement insert() {
        return new InsertStatement(fieldScanner.getDefaultConverter());
    }

    /**
     * 给一个insert语句
     *
     * @param clazz           语句设计的实体类，这个类用于扫描转换器，表名
     * @param scannerCallback 属性扫描回调
     * @param entityName      参数名称
     * @return 语句
     */
    public InsertStatement insert(Class<?> clazz, BiFunction<FieldScanner, Class<?>, Collection<String>> scannerCallback, String entityName) {
        return new InsertStatement(fieldScanner.getConverter(clazz)).table(clazz).columns(scannerCallback.apply(fieldScanner, clazz), entityName);
    }

    /**
     * 给一个insert语句
     *
     * @param clazz           语句设计的实体类，这个类用于扫描转换器，表名
     * @param scannerCallback 字段扫描回调
     * @param collection      批量插入集合参数名称
     * @param item            批量插入元素名称
     * @return 语句
     */
    public InsertStatement insert(Class<?> clazz, BiFunction<FieldScanner, Class<?>, Collection<String>> scannerCallback, String collection, String item) {
        return new InsertStatement(fieldScanner.getConverter(clazz)).table(clazz).batch(scannerCallback.apply(fieldScanner, clazz), collection, item);
    }

    /**
     * 给一个update语句
     *
     * @return 语句
     */
    public UpdateStatement update() {
        return new UpdateStatement(fieldScanner.getDefaultConverter());
    }

    /**
     * 给一个update语句
     *
     * @param clazz 实体类，仅用来设置表名
     * @return 语句
     */
    public UpdateStatement update(Class<?> clazz) {
        return new UpdateStatement(fieldScanner.getConverter(clazz)).table(clazz);
    }

    /**
     * 给一个update语句
     *
     * @param clazz           语句设计的实体类，这个类用于扫描转换器，表名
     * @param scannerCallback 属性扫描回调
     * @param entityName      参数名称
     * @return 语句
     */
    public UpdateStatement update(Class<?> clazz, BiFunction<FieldScanner, Class<?>, Collection<String>> scannerCallback, String entityName) {
        return new UpdateStatement(fieldScanner.getConverter(clazz)).table(clazz).columns(scannerCallback.apply(fieldScanner, clazz), entityName);
    }

    /**
     * 给一个删除语句
     *
     * @return 语句
     */
    public DeleteStatement delete() {
        return new DeleteStatement();
    }

    /**
     * 给一个删除语句
     *
     * @param clazz 实体类，用于设定表名
     * @return 语句
     */
    public DeleteStatement delete(Class<?> clazz) {
        return new DeleteStatement().table(clazz);
    }
}
