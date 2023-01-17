package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.BasicElement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * 列的定义
 *
 * @author zhanghan30
 * @date 2022/9/23 21:45
 */
public class Column extends BasicElement {
    /**
     * 别名(自动加重音)
     */
    @Nullable
    @Getter
    private final String alias;
    /**
     * 表名(自动加重音)
     */
    @Nullable
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String table;
    /**
     * 列名(不见得要加重音，比如是个表达式)
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String columnExpression;
    /**
     * 加不加重音符号
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private boolean addGraveAccent;

    /**
     * 自动加``
     *
     * @param column 列名
     */
    public Column(String column) {
        this(null, column, true, null);
    }

    /**
     * 列名
     *
     * @param columnExpression 列或表达式
     * @param addGraveAccent   是不是+ ``
     */
    public Column(String columnExpression, boolean addGraveAccent) {
        this(null, columnExpression, addGraveAccent, null);
    }

    /**
     * 表.列名
     *
     * @param table  表名
     * @param column `列名`
     */
    public Column(@Nullable String table, String column) {
        this(table, column, true);
    }

    /**
     * 表.列名
     *
     * @param table            表名
     * @param columnExpression 列名或表达式
     * @param addGraveAccent   要不要加`
     */
    public Column(@Nullable String table, String columnExpression, boolean addGraveAccent) {
        this(table, columnExpression, addGraveAccent, null);
    }

    /**
     * 表.列 AS 别名
     *
     * @param table  表名
     * @param column `列名`
     * @param alias  `别名`
     */
    public Column(@Nullable String table, String column, @Nullable String alias) {
        this(table, column, true, alias);
    }

    /**
     * 全量构建
     *
     * @param table            表名
     * @param columnExpression 列名或表达式
     * @param addGraveAccent   加重音？
     * @param alias            别名
     */
    public Column(@Nullable String table, String columnExpression, boolean addGraveAccent, @Nullable String alias) {
        this.table = table;
        this.columnExpression = columnExpression;
        this.addGraveAccent = addGraveAccent;
        this.alias = alias;
    }

    @Override
    public String build() {
        StringBuilder columnExpressionBuilder = new StringBuilder();
        if (StringUtils.hasText(table)) {
            columnExpressionBuilder.append(addGraveAccent(table));
            columnExpressionBuilder.append(".");
        }
        columnExpressionBuilder.append(addGraveAccent ? addGraveAccent(columnExpression) : columnExpression);
        if (StringUtils.hasText(alias)) {
            columnExpressionBuilder.append("AS ");
            columnExpressionBuilder.append(addGraveAccent(alias));
        }
        return columnExpressionBuilder.toString();
    }

    /**
     * 获取列名
     * <p>
     * 其实就是有alias返回alias，否则返回columnExpression
     *
     * @return 列名
     */
    public String getColumnName() {
        return StringUtils.hasText(alias) ? alias : columnExpression;
    }
}
