package com.jdcloud.gardener.fragrans.api.idempotent.engine.annotation;

import java.lang.annotation.*;

/**
 * 幂等防护的核心注解
 *
 * @author zhanghan30
 * @date 2022/2/24 2:10 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface IdempotentApi {
}
