package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.BasicElement;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BasicCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BooleanCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
    private BasicCriteria queryCriteria;
    /**
     * having条件
     */
    private BasicCriteria havingCriteria;
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
     * @param column         列名
     * @param addGraveAccent 是否要添加重音符号
     * @return 语句
     */
    public SelectStatement column(String column, boolean addGraveAccent) {
        return column(null, column, addGraveAccent, null);
    }

    /**
     * 添加一个单独的列以及别名，列名由参数决策，但别名将被自动附加``符号
     *
     * @param column         列名
     * @param addGraveAccent 列名是否要加重音符号
     * @param alias          别名
     * @return 语句
     */
    public SelectStatement column(String column, boolean addGraveAccent, String alias) {
        return column(null, column, addGraveAccent, alias);
    }

    /**
     * 添加一个单独的列以及别名，列名由参数决策，但别名将被自动附加``符号
     * <p>
     * 表名也由参数决定
     *
     * @param table          表名
     * @param column         列名
     * @param addGraveAccent 列名是否要加重音符号
     * @param alias          别名
     * @return 语句
     */
    public SelectStatement column(@Nullable String table, String column, boolean addGraveAccent, @Nullable String alias) {
        return column(new Column(table, column, addGraveAccent, alias));
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
    public SelectStatement on(BasicCriteria criteria) {
        this.onCriteria.and(criteria);
        return this;
    }

    /**
     * 给定一个查询条件，可以使用{@link BooleanCriteria}进行逻辑组合
     *
     * @param criteria 查询条件
     * @return 语句
     */
    public SelectStatement where(BasicCriteria criteria) {
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
     * @param column         列名
     * @param addGraveAccent 是不是加重音符号
     * @return 语句
     */
    public SelectStatement groupBy(String column, boolean addGraveAccent) {
        return groupBy(new Column(column, addGraveAccent));
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
    public SelectStatement having(BasicCriteria criteria) {
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
     * @param column         列名
     * @param addGraveAccent 是否加重音符号
     * @return 语句
     */
    public SelectStatement orderBy(String column, boolean addGraveAccent) {
        return orderBy(column, addGraveAccent, null);
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
     * @param column         列名
     * @param addGraveAccent 是否加重音符号
     * @param order          升降序
     * @return 语句
     */
    public SelectStatement orderBy(String column, boolean addGraveAccent, @Nullable Order order) {
        this.orderByColumns.add(
                new OrderByElement(
                        new Column(column, addGraveAccent),
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

    @Override
    protected String buildInternally() {
        return appendLimit(
                appendOrderBy(
                        appendHavingCriteria(
                                appendGroupByColumns(
                                        appendQueryCriteria(
                                                appendJoin(String.format("SELECT %s FROM %s",
                                                        (this.countFoundRows ? "SQL_CALC_FOUND_ROWS " : "") + queryColumns.stream().map(Column::build).collect(Collectors.joining(",")),
                                                        this.getTable().build()
                                                )),
                                                queryCriteria
                                        )
                                )
                        )
                )
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
        if (!CollectionUtils.isEmpty(this.orderByColumns)) {
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
        if (this.offset != null && this.size != null) {
            return String.format("%s LIMIT %d,%d", statement, this.offset, this.size);
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
    private class OrderByElement extends BasicElement {
        private final Column column;
        private final Order order;

        @Override
        public String build() {
            return column.build() + " " + order;
        }
    }

    @AllArgsConstructor
    private class JoinElement extends BasicElement {
        private Join join;
        private RecordSet element;

        @Override
        public String build() {
            return join.getType() + String.format(" %s", element.build());
        }
    }

}
