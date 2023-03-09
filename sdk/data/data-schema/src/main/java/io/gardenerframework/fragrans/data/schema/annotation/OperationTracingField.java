package io.gardenerframework.fragrans.data.schema.annotation;

import java.lang.annotation.*;

/**
 * 代表属性是由用来添加操作跟踪信息的，比如
 * <p>
 * 操作人
 * <p>
 * 删除人
 * <p>
 * 创建人
 * <p>
 * 这样的属性
 *
 * @author zhanghan
 * @date 2021/4/15 01:08
 * @since 1.0.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationTracingField {
}
