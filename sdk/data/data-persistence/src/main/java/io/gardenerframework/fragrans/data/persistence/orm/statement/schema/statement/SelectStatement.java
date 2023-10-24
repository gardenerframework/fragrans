package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement;

import io.gardenerframework.fragrans.data.persistence.orm.database.Database;
import io.gardenerframework.fragrans.data.persistence.orm.statement.exception.UnsupportedDriverException;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.SqlElement;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.Column;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.BooleanCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.DatabaseCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/6/15 2:39 下午
 */
public class SelectStatement extends BasicStatement<SelectStatement> {
    public static final String FOUND_ROWS_VARIABLE_NAME = "FOUND_ROWS";
    /**
     * 列清单
     */
    @Getter
    private final Collection<Column> queryColumns = new LinkedList<>();
    /**
     * join的表或者语句清单
     */
    private final Collection<JoinElement> joins = new LinkedList<>();
    /**
     * group by的列清单
     */
    private final Collection<Column> groupByColumns = new LinkedList<>();
    /**
     * order by的列清单
     */
    private final Collection<OrderByElement> orderByColumns = new LinkedList<>();
    /**
     * 连接条件
     */
    private final MatchAllCriteria onCriteria = new MatchAllCriteria();
    /**
     * 是否记录找到多少行
     */
    private boolean countFoundRows = false;
    /**
     * 查询条件
     */
    private DatabaseCriteria queryCriteria;
    /**
     * having条件
     */
    private DatabaseCriteria havingCriteria;
    /**
     * 记录偏移量
     */
    private Long offset;
    /**
     * 大小
     */
    private Long size;

    /**
     * 是否数找到多少行数据
     *
     * @param enable 是否激活
     * @return 语句
     */
    public SelectStatement countFoundRows(boolean enable) {
        this.countFoundRows = enable;
        return this;
    }

    /**
     * 清除所有查询列
     *
     * @return 语句
     */
    public SelectStatement clearAllQueryColumns() {
        this.queryColumns.clear();
        return this;
    }

    /**
     * 插入多个select查询结果列，每一个列名将被自动附加``符号
     *
     * @param columns 结果列清单
     * @return 语句
     */
    public SelectStatement columns(Collection<String> columns) {
        return columns(null, columns);
    }

    /**
     * 插入多个select查询结果列，每一个列名和表名会将被自动附加``符号
     *
     * @param table   表明
     * @param columns 列名
     * @return 语句
     */
    public SelectStatement columns(@Nullable String table, Collection<String> columns) {
        return columns(table, columns, filter -> true);
    }

    /**
     * 插入多个select查询结果列，每一个列名将由gravyAccentFilter决定是否添加重音符号
     *
     * @param columns           结果列清单
     * @param graveAccentFilter 列名是否添加重音符号的过滤器
     * @return 语句
     */
    public SelectStatement columns(Collection<String> columns, Function<String, Boolean> graveAccentFilter) {
        return columns(null, columns, graveAccentFilter);
    }

    /**
     * 插入多个select查询结果列，这些列包含表明并每一个列名将由gravyAccentFilter决定是否添加重音符号
     * <p>
     * 表名自动会加重音符号
     *
     * @param table                   表名
     * @param columns                 列名
     * @param columnGravyAccentFilter 列是否加重音
     * @return 语句
     */
    public SelectStatement columns(@Nullable String table, Collection<String> columns, Function<String, Boolean> columnGravyAccentFilter) {
        columns.forEach(
                column -> this.queryColumns.add(new Column(table, column, Boolean.TRUE.equals(columnGravyAccentFilter.apply(column))))
        );
        return this;
    }

    /**
     * 添加一个单独的列，列名将被自动附加``符号
     *
     * @param column 列名
     * @return 语句
     */
    public SelectStatement column(String column) {
        return column(null, column);
    }

    /**
     * 添加一个单独的列，列名将被自动附加``符号
     *
     * @param table  表名
     * @param column 列名
     * @return 语句
     */
    public SelectStatement column(String table, String column) {
        return column(table, column, true, null);
    }

    /**
     * 添加一个单独的列
     *
     * @param column               列名
     * @param addDelimitIdentifier 是否要添加重音符号
     * @return 语句
     */
    public SelectStatement column(String column, boolean addDelimitIdentifier) {
        return column(null, column, addDelimitIdentifier, null);
    }

    /**
     * 添加一个单独的列以及别名，列名由参数决策，但别名将被自动附加``符号
     *
     * @param column               列名
     * @param addDelimitIdentifier 列名是否要加重音符号
     * @param alias                别名
     * @return 语句
     */
    public SelectStatement column(String column, boolean addDelimitIdentifier, String alias) {
        return column(null, column, addDelimitIdentifier, alias);
    }

    /**
     * 添加一个单独的列以及别名，列名由参数决策，但别名将被自动附加``符号
     * <p>
     * 表名也由参数决定
     *
     * @param table                表名
     * @param column               列名
     * @param addDelimitIdentifier 列名是否要加重音符号
     * @param alias                别名
     * @return 语句
     */
    public SelectStatement column(@Nullable String table, String column, boolean addDelimitIdentifier, @Nullable String alias) {
        return column(new Column(table, column, addDelimitIdentifier, alias));
    }

    /**
     * 添加一个单独的列
     *
     * @param column 列
     * @return 语句
     */
    public SelectStatement column(Column column) {
        this.queryColumns.add(column);
        return this;
    }


    /**
     * 左连接
     *
     * @param table 表名
     * @return 语句
     */
    public SelectStatement leftJoin(String table) {
        this.joins.add(new JoinElement(Join.LEFT, new RecordSet(table)));
        return this;
    }

    /**
     * 左链接
     *
     * @param subQuery 子查询语句
     * @param alias    别名
     * @return 当前语句
     */
    @SuppressWarnings("rawtypes")
    public SelectStatement leftJoin(BasicStatement subQuery, String alias) {
        this.joins.add(new JoinElement(Join.LEFT, new RecordSet(subQuery, alias)));
        return this;
    }

    /**
     * 右连接
     *
     * @param table 表名
     * @return 语句
     */
    public SelectStatement rightJoin(String table) {
        this.joins.add(new JoinElement(Join.RIGHT, new RecordSet(table)));
        return this;
    }

    /**
     * 右链接
     *
     * @param subQuery 子查询语句
     * @param alias    别名
     * @return 当前语句
     */
    @SuppressWarnings("rawtypes")
    public SelectStatement rightJoin(BasicStatement subQuery, String alias) {
        this.joins.add(new JoinElement(Join.RIGHT, new RecordSet(subQuery, alias)));
        return this;
    }

    /**
     * 内连接
     *
     * @param table 表名或子查询
     * @return 语句
     */
    public SelectStatement join(String table) {
        this.joins.add(new JoinElement(Join.INNER, new RecordSet(table)));
        return this;
    }

    /**
     * 内链接
     *
     * @param subQuery 子查询语句
     * @param alias    别名
     * @return 当前语句
     */
    @SuppressWarnings("rawtypes")
    public SelectStatement join(BasicStatement subQuery, String alias) {
        this.joins.add(new JoinElement(Join.INNER, new RecordSet(subQuery, alias)));
        return this;
    }

    /**
     * 连接条件
     *
     * @param criteria 条件
     * @return 语句
     */
    public SelectStatement on(DatabaseCriteria criteria) {
        this.onCriteria.and(criteria);
        return this;
    }

    /**
     * 给定一个查询条件，可以使用{@link BooleanCriteria}进行逻辑组合
     *
     * @param criteria 查询条件
     * @return 语句
     */
    public SelectStatement where(DatabaseCriteria criteria) {
        this.queryCriteria = criteria;
        return this;
    }

    /**
     * 聚合分组列名，自动加重音符号
     *
     * @param column 列名
     * @return 语句
     */
    public SelectStatement groupBy(String column) {
        return groupBy(column, true);
    }

    /**
     * 聚合分组列名
     *
     * @param column               列名
     * @param addDelimitIdentifier 是不是加重音符号
     * @return 语句
     */
    public SelectStatement groupBy(String column, boolean addDelimitIdentifier) {
        return groupBy(new Column(column, addDelimitIdentifier));
    }

    /**
     * 聚合分组列名
     *
     * @param column 列名
     * @return 语句
     */
    public SelectStatement groupBy(Column column) {
        this.groupByColumns.add(column);
        return this;
    }

    /**
     * 聚合分组列名，自动加重音符号
     *
     * @param columns 列名
     * @return 语句
     */
    public SelectStatement groupBy(Collection<String> columns) {
        return groupBy(columns, column -> true);
    }

    /**
     * 聚合分组列名
     *
     * @param columns           列名
     * @param graveAccentFilter 由过滤器决定是不是加重音符号
     * @return 语句
     */
    public SelectStatement groupBy(Collection<String> columns, Function<String, Boolean> graveAccentFilter) {
        columns.forEach(
                column -> this.groupByColumns.add(
                        new Column(column, Boolean.TRUE.equals(graveAccentFilter.apply(column)))
                )
        );
        return this;
    }

    /**
     * 给定having的条件
     *
     * @param criteria 条件，使用{@link BooleanCriteria}进行条件组合
     * @return 语句
     */
    public SelectStatement having(DatabaseCriteria criteria) {
        this.havingCriteria = criteria;
        return this;
    }

    /**
     * 指定一个排序列进行升序，对应的列名自动会加重音符号
     *
     * @param column 列名
     * @return 语句
     */
    public SelectStatement orderBy(String column) {
        return orderBy(column, true);
    }

    /**
     * 指定一个排序列进行升序
     *
     * @param column               列名
     * @param addDelimitIdentifier 是否加重音符号
     * @return 语句
     */
    public SelectStatement orderBy(String column, boolean addDelimitIdentifier) {
        return orderBy(column, addDelimitIdentifier, null);
    }

    /**
     * 指定一个排序列进行升序，对应的列名自动会加重音符号
     *
     * @param column 列名
     * @param order  升降序
     * @return 语句
     */
    public SelectStatement orderBy(String column, @Nullable Order order) {
        return orderBy(column, true, order);
    }

    /**
     * 指定一个排序列进行升序
     *
     * @param column               列名
     * @param addDelimitIdentifier 是否加重音符号
     * @param order                升降序
     * @return 语句
     */
    public SelectStatement orderBy(String column, boolean addDelimitIdentifier, @Nullable Order order) {
        this.orderByColumns.add(
                new OrderByElement(
                        new Column(column, addDelimitIdentifier),
                        order == null ? Order.ASC : order
                )
        );
        return this;
    }

    /**
     * limit条件
     *
     * @param size 多大
     * @return 语句
     */
    public SelectStatement limit(long size) {
        return limit(0, size);
    }

    /**
     * limit条件
     *
     * @param offset 从哪个记录开始
     * @param size   多大
     * @return 语句
     */
    public SelectStatement limit(long offset, long size) {
        this.offset = offset;
        this.size = size;
        return this;
    }

    /**
     * 套用limit执行分页
     *
     * @param no   要访问的页码
     * @param size 页大小
     * @return 语句
     */
    public SelectStatement pagination(long no, long size) {
        limit((no - 1) * size, size);
        return this;
    }

    @Override
    protected String buildInternally() {
        return appendLimit(
                appendOrderBy(
                        appendHavingCriteria(
                                appendGroupByColumns(
                                        appendQueryCriteria(
                                                appendJoin(buildMainStatement()),
                                                queryCriteria
                                        )
                                )
                        )
                )
        );
    }

    /**
     * 创建最原始的语句
     *
     * @return select from xxx这种没有任何条件的语句
     */
    private String buildMainStatement() {
        return String.format("SELECT %s FROM %s",
                countFoundRows ? "COUNT(1)" : queryColumns.stream().map(Column::build).collect(Collectors.joining(",")),
                this.getTable().build()
        );
    }

    private String appendJoin(String statement) {
        if (!CollectionUtils.isEmpty(joins)) {
            //join语句
            String joinStatement = joins.stream().map(
                    JoinElement::build
            ).collect(Collectors.joining(String.format("%n")));
            return String.format("%s %s ON (%s)", statement, joinStatement, onCriteria.build());
        }
        return statement;
    }

    /**
     * 增加group by 子句
     *
     * @param statement 语句
     * @return 增加完的语句
     */
    private String appendGroupByColumns(String statement) {
        if (!CollectionUtils.isEmpty(this.groupByColumns)) {
            return String.format("%s GROUP BY %s", statement, groupByColumns.stream().map(Column::build).collect(Collectors.joining(",")));
        }
        return statement;
    }

    /**
     * 添加having子句
     *
     * @param statement 语句
     * @return 增加完的语句
     */
    private String appendHavingCriteria(String statement) {
        if (!CollectionUtils.isEmpty(this.groupByColumns) && havingCriteria != null) {
            return String.format("%s HAVING %s", statement, havingCriteria.build());
        }
        return statement;
    }

    /**
     * 添加order by子句
     *
     * @param statement 语句
     * @return 增加完的语句
     */
    private String appendOrderBy(String statement) {
        if (!CollectionUtils.isEmpty(this.orderByColumns) && !this.countFoundRows) {
            return String.format("%s ORDER BY %s", statement, orderByColumns.stream().map(
                    OrderByElement::build
            ).collect(Collectors.joining(",")));
        }
        return statement;
    }

    /**
     * 添加limit子句
     *
     * @param statement 语句
     * @return 增加完的语句
     */
    private String appendLimit(String statement) {
        if (this.offset != null && this.size != null && !this.countFoundRows) {
            DatabaseDriver driver = Database.getDriver();
            switch (driver) {
                case MYSQL:
                    return String.format("%s LIMIT %d,%d", statement, this.offset, this.size);
                case SQLSERVER:
                    return String.format("%s OFFSET %d ROWS FETCH NEXT %d ROWS ONLY", statement, this.offset, this.size);
                default:
                    throw new UnsupportedDriverException(driver);
            }
        }
        return statement;
    }

    public enum Order {
        /**
         * 生序
         */
        ASC,
        /**
         * 降序
         */
        DESC
    }

    public enum Join {
        /**
         * inner join
         */
        INNER("INNER JOIN"),
        /**
         * left join
         */
        LEFT("LEFT JOIN"),
        /**
         * right join
         */
        RIGHT("RIGHT JOIN");

        @Getter
        private final String type;

        Join(String type) {
            this.type = type;
        }
    }

    /**
     * 一个内部使用的排序元素
     */
    @AllArgsConstructor
    private class OrderByElement implements SqlElement {
        private final Column column;
        private final Order order;

        @Override
        public String build() {
            return column.build() + " " + order;
        }
    }

    @AllArgsConstructor
    private class JoinElement implements SqlElement {
        private Join join;
        private RecordSet element;

        @Override
        public String build() {
            return join.getType() + String.format(" %s", element.build());
        }
    }

}
