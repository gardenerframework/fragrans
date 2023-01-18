package io.gardenerframework.fragrans.data.schema.annotation;

import java.lang.annotation.*;

/**
 * 代表列是由特定的操作来更新的，而不是常规的属性覆盖
 * <p>
 * 也就是单独更新的列
 * <p>
 * 如修改密码，启用或停用，更改申批状态等
 *
 * @author zhanghan
 * @date 2021/4/15 01:08
 * @since 1.0.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipInGenericUpdateOperation {
}
