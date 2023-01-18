package io.gardenerframework.fragrans.data.persistence.orm.statement.annotation;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * @author zhanghan30
 * @date 2022/9/23 23:45
 */
public abstract class TableNameUtils {
    private TableNameUtils() {

    }

    /**
     * 扫描注解获得表名
     *
     * @param clazz 类
     * @return 表名
     */
    public static String getTableName(Class<?> clazz) {
        TableName annotation = AnnotationUtils.findAnnotation(clazz, TableName.class);
        Assert.notNull(annotation, clazz + " must have @TableName annotation");
        return annotation.value();
    }
}
