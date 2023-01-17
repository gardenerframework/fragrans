package com.jdcloud.gardener.fragrans.api.standard.error.exception;

import java.util.Map;

/**
 * 表明当前异常能够携带错误详情
 *
 * @author zhanghan30
 * @date 2022/5/9 11:06 下午
 */
@FunctionalInterface
public interface ApiErrorDetailsSupplier {
    /**
     * 给出详情
     *
     * @return 详情
     */
    Map<String, Object> getDetails();
}
