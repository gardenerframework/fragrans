package io.gardenerframework.fragrans.data.persistence.orm.entity.converter;


/**
 * 将一个类的属性字段转换为数据库的列名
 *
 * @author zhanghan30
 * @date 2022/6/14 5:11 下午
 */
public interface ColumnNameConverter {
    /**
     * 转换
     *
     * @param field 字段
     * @return 列名
     */
    String fieldToColumn(String field);

    /**
     * 转换
     *
     * @param column 列名
     * @return 字段名
     */
    String columnToField(String column);
}
