package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column;

import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/9/24 18:07
 */
public class JsonObjectColumn extends Column {

    protected JsonObjectColumn(@Nullable String table, Collection<String> columns, Function<String, String> columnToFieldMapper) {
        this(table, columns, columnToFieldMapper, null);
    }

    protected JsonObjectColumn(Collection<String> columns, Function<String, String> columnToFieldMapper) {
        this(null, columns, columnToFieldMapper);
    }

    /**
     * 通过列名聚合为json字段
     *
     * @param columns             列清单
     * @param columnToFieldMapper 转换器
     * @param alias               字段的别名
     */
    public JsonObjectColumn(Collection<String> columns, Function<String, String> columnToFieldMapper, @Nullable String alias) {
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
    public JsonObjectColumn(@Nullable String table, Collection<String> columns, Function<String, String> columnToFieldMapper, @Nullable String alias) {
        super("", false);
        this.setColumnExpression(buildJsonObject(table, columns, columnToFieldMapper, alias));
    }

    /**
     * 去创建json object
     *
     * @param table               表名
     * @param columns             列清单
     * @param columnToFieldMapper 转换器
     * @param alias               别名
     * @return json_object表达式
     */
    private String buildJsonObject(@Nullable String table, Collection<String> columns, Function<String, String> columnToFieldMapper, @Nullable String alias) {
        Collection<JsonField> fields = new LinkedList<>();
        columns.forEach(
                column -> fields.add(new JsonField(
                                table,
                                columnToFieldMapper.apply(column),
                                column,
                                true
                        )
                )
        );
        return String.format("JSON_OBJECT(%s)%s",
                fields.stream().map(Object::toString).collect(Collectors.joining(",")),
                StringUtils.hasText(alias) ? " AS " + addGraveAccent(alias) : ""
        );

    }

    @AllArgsConstructor
    private class JsonField {
        @Nullable
        private String table;
        private String field;
        private String column;
        private boolean addGraveAccent;

        @Override
        public String toString() {
            return String.format("\"%s\", %s", field,
                    (StringUtils.hasText(table) ? (addGraveAccent(table) + ".") : "")
                            + (addGraveAccent ? addGraveAccent(column) : column));
        }
    }
}
