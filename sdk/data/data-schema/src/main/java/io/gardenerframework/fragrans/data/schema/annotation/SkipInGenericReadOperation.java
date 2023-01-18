package io.gardenerframework.fragrans.data.schema.annotation;

import java.lang.annotation.*;

/**
 * 代表列是由特定和明确的操作来读取的
 * <p>
 * 特定和明确的意思是: 非select *
 * <p>
 * 如商品的库存，用户的密码等
 *
 * @author zhanghan
 * @date 2021/4/15 01:08
 * @since 1.0.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipInGenericReadOperation {
}
