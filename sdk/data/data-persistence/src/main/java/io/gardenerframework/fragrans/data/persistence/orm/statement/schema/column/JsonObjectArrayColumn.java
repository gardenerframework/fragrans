package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author zhanghan30
 * @date 2022/9/24 18:24
 */
public class JsonObjectArrayColumn extends JsonObjectColumn {
    /**
     * 通过列名聚合为json字段
     *
     * @param columns             列清单
     * @param columnToFieldMapper 转换器
     * @param alias               字段的别名
     */
    public JsonObjectArrayColumn(Collection<String> columns, Function<String, String> columnToFieldMapper, String alias) {
        this(null, columns, columnToFieldMapper, alias);
    }

    /**
     * 通过列名聚合为json字段
     *
     * @param table               表名
     * @param columns             列清单
     * @param columnToFieldMapper 转换器
     * @param alias               字段的别名
     */
    public JsonObjectArrayColumn(@Nullable String table, Collection<String> columns, Function<String, String> columnToFieldMapper, String alias) {
        super(table, columns, columnToFieldMapper);
        this.setColumnExpression(
                String.format("JSON_ARRAYAGG(%s)%s", this.getColumnExpression(),
                        StringUtils.hasText(alias) ? " AS " + addDelimitIdentifier(alias) : "")
        );
    }
}
