package com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation;

import java.lang.annotation.*;

/**
 * 表名
 *
 * @author zhanghan
 * @date 2021/9/27 16:53
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableName {
    String value();
}
