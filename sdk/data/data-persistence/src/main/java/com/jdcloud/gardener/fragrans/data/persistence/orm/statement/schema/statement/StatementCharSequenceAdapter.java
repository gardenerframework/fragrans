package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement;

import lombok.Getter;

/**
 * 对{@link org.apache.ibatis.builder.annotation.ProviderMethodResolver}执行的适配器
 *
 * @author zhanghan30
 * @date 2022/10/20 17:02
 */
public class StatementCharSequenceAdapter<S extends BasicStatement<S>> implements CharSequence {
    @Getter
    private final S statement;
    private final String sql;

    public StatementCharSequenceAdapter(S statement) {
        this.statement = statement;
        this.sql = statement.build();
    }

    public StatementCharSequenceAdapter(S statement, boolean noScriptTag) {
        this.statement = statement;
        this.sql = statement.build(noScriptTag);
    }

    @Override
    public int length() {
        return sql.length();
    }

    @Override
    public char charAt(int index) {
        return sql.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return sql.subSequence(start, end);
    }

    @Override
    public String toString() {
        return sql;
    }
}
