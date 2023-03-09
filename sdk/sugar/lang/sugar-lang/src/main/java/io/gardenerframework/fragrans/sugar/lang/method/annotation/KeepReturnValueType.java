package io.gardenerframework.fragrans.sugar.lang.method.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 保持原始类型
 *
 * @author zhanghan30
 * @date 2022/9/14 4:52 下午
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface KeepReturnValueType {
}
