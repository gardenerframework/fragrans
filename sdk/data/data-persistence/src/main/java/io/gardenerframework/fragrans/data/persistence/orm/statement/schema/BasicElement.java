package io.gardenerframework.fragrans.data.persistence.orm.statement.schema;

/**
 * @author zhanghan30
 * @date 2022/6/14 7:08 下午
 */
public abstract class BasicElement {
    /**
     * 给出元素对应的语句字符串
     *
     * @return 语句字符串
     */
    public abstract String build();


    /**
     * 为字段加上mysql的``对
     *
     * @param element 要加的东西
     * @return 加完的结果
     */
    protected String addGraveAccent(String element) {
        return String.format("`%s`", element);
    }
}
