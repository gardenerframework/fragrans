package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column;

import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.SqlElement;
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
public class Column implements SqlElement {
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
     * 加不加界定符
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private boolean addDelimitIdentifier;

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
     * @param columnExpression     列或表达式
     * @param addDelimitIdentifier 是不是加上界定符
     */
    public Column(String columnExpression, boolean addDelimitIdentifier) {
        this(null, columnExpression, addDelimitIdentifier, null);
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
     * @param table                表名
     * @param columnExpression     列名或表达式
     * @param addDelimitIdentifier 是不是加上界定符
     */
    public Column(@Nullable String table, String columnExpression, boolean addDelimitIdentifier) {
        this(table, columnExpression, addDelimitIdentifier, null);
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
     * @param table                表名
     * @param columnExpression     列名或表达式
     * @param addDelimitIdentifier 是不是加上界定符
     * @param alias                别名
     */
    public Column(@Nullable String table, String columnExpression, boolean addDelimitIdentifier, @Nullable String alias) {
        this.table = table;
        this.columnExpression = columnExpression;
        this.addDelimitIdentifier = addDelimitIdentifier;
        this.alias = alias;
    }

    @Override
    public String build() {
        StringBuilder columnExpressionBuilder = new StringBuilder();
        if (StringUtils.hasText(table)) {
            columnExpressionBuilder.append(addDelimitIdentifier(table));
            columnExpressionBuilder.append(".");
        }
        columnExpressionBuilder.append(addDelimitIdentifier ? addDelimitIdentifier(columnExpression) : columnExpression);
        if (StringUtils.hasText(alias)) {
            columnExpressionBuilder.append("AS ");
            columnExpressionBuilder.append(addDelimitIdentifier(alias));
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
