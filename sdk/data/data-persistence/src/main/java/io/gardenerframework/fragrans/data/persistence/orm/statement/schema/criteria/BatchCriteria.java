package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria;

/**
 * @author zhanghan30
 * @date 2022/6/14 7:25 下午
 */
public class BatchCriteria implements DatabaseCriteria {
    /**
     * 集合名称
     */
    private String collection;
    /**
     * 元素名称
     */
    private String item;
    /**
     * 分割符号
     */
    private Separator separator = Separator.OR;
    /**
     * 实际的查询条件
     */
    private DatabaseCriteria criteria;

    /**
     * 用于集合名称在某个参数的属性内的情况
     *
     * @param collectionHolder   集合在哪个参数中
     * @param collectionProperty 集合的属性名称
     * @return 语句
     */
    public BatchCriteria collection(String collectionHolder, String collectionProperty) {
        return collection(String.format("%s.%s", collectionHolder, collectionProperty));
    }

    /**
     * 用于集合名称在某个参数的属性内的情况
     *
     * @param collection 集合名称
     * @return 语句
     */
    public BatchCriteria collection(String collection) {
        this.collection = collection;
        return this;
    }

    public BatchCriteria item(String item) {
        this.item = item;
        return this;
    }

    public BatchCriteria separator(Separator separator) {
        this.separator = separator;
        return this;
    }

    public BatchCriteria criteria(DatabaseCriteria criteria) {
        this.criteria = criteria;
        return this;
    }

    @Override
    public String build() {
        return String.format("(" +
                "<foreach item=\"%s\" collection=\"%s\" separator=\"%s\">%n" +
                "       (%s)%n" +
                "</foreach>%n" +
                ")", item, collection, separator, criteria.build());
    }

    public enum Separator {
        /**
         * 并且
         */
        AND,
        /**
         * 或者
         */
        OR
    }
}
