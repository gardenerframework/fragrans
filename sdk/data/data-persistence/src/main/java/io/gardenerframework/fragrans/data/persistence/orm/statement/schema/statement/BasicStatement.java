package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.statement;

import io.gardenerframework.fragrans.data.persistence.orm.statement.annotation.TableNameUtils;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.BasicElement;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.DatabaseCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.MatchAnyCriteria;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2022/6/14 6:35 下午
 */
@Getter
public abstract class BasicStatement<S extends BasicStatement<S>> extends BasicElement {
    /**
     * 表名称
     */
    @Getter(AccessLevel.PROTECTED)
    private RecordSet table;

    /**
     * 设置表名
     *
     * @param table 表名
     * @return 语句
     */
    @SuppressWarnings("unchecked")
    public S table(String table) {
        this.table = new RecordSet(table);
        return (S) this;
    }

    /**
     * 设置表名
     *
     * @param clazz 类
     * @return 语句
     */
    public S table(Class<?> clazz) {
        return table(TableNameUtils.getTableName(clazz));
    }

    /**
     * 设置表为子查询，别名自动加``
     *
     * @param subQuery 子查询
     * @param alias    子查询的别名
     * @return 语句
     */
    public S table(S subQuery, String alias) {
        return table(subQuery, alias, true);
    }

    /**
     * 使用子查询作为表
     *
     * @param subQuery       子查询
     * @param alias          子查询的名称
     * @param addGraveAccent 是否加重音
     * @return 语句
     */
    @SuppressWarnings("unchecked")
    public S table(S subQuery, String alias, boolean addGraveAccent) {
        //关闭script标签
        this.table = new RecordSet(
                subQuery,
                alias,
                addGraveAccent
        );
        return (S) this;
    }

    /**
     * 给出最终的语句
     *
     * @return 语句
     */
    protected abstract String buildInternally();

    @Override
    public String build() {
        return build(false);
    }

    /**
     * 构建语句
     *
     * @param noScriptTag 是否去掉script标签
     * @return 语句
     */
    public String build(boolean noScriptTag) {
        Assert.notNull(table, "table must not be null");
        return String.format(
                "%s%n" +
                        "%s" +
                        "%n%s",
                noScriptTag ? "" : "<script>",
                buildInternally(),
                noScriptTag ? "" : "</script>"
        );
    }

    /**
     * 将语句加上where条件
     *
     * @param statement 语句
     * @param criteria  条件
     * @return 加完的结果
     */
    protected String appendQueryCriteria(String statement, @Nullable DatabaseCriteria criteria) {
        if (criteria != null ||
                (criteria instanceof MatchAllCriteria && !((MatchAllCriteria) criteria).isEmpty()) ||
                (criteria instanceof MatchAnyCriteria && !((MatchAnyCriteria) criteria).isEmpty())
        ) {
            return String.format("%s WHERE (%s)", statement, criteria.build());
        }
        return statement;
    }

    /**
     * 记录集
     * <p>
     * 或者是表
     * <p>
     * 或者是子查询
     */
    protected static class RecordSet extends BasicElement {
        /**
         * 表名
         */
        private final String table;
        /**
         * 子查询
         */
        @SuppressWarnings("rawtypes")
        private final BasicStatement subQuery;
        /**
         * 别名
         */
        private final String alias;

        private final boolean addGraveAccent;

        public RecordSet(String table) {
            this.table = table;
            this.subQuery = null;
            this.alias = null;
            this.addGraveAccent = false;
        }

        @SuppressWarnings("rawtypes")
        public RecordSet(BasicStatement subQuery, String alias) {
            this(subQuery, alias, true);
        }

        @SuppressWarnings("rawtypes")
        public RecordSet(BasicStatement subQuery, String alias, boolean addGraveAccent) {
            this.table = null;
            this.subQuery = subQuery;
            this.alias = alias;
            this.addGraveAccent = addGraveAccent;
        }

        @Override
        public String build() {
            return table != null ? addGraveAccent(table) :
                    String.format("((%s) %s)",
                            Objects.requireNonNull(subQuery).build(true),
                            addGraveAccent ? addGraveAccent(alias) : alias
                    );
        }
    }
}