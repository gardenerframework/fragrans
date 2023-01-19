package io.gardenerframework.fragrans.api.standard.error.exception;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 代表类用于{@code response.sendError}这样的报错的异常实体兜底
 * <p>
 * 通常用于没有实际异常发生，但是最终作为错误要输出的场景
 *
 * @author zhanghan
 * @date 2020-11-11 10:14
 * @since 1.0.0
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface HttpStatusRepresentative {
}