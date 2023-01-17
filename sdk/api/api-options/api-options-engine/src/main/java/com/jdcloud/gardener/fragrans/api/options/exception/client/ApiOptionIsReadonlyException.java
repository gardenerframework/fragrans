package com.jdcloud.gardener.fragrans.api.options.exception.client;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.BadRequestException;

/**
 * @author zhanghan30
 * @date 2022/5/10 4:06 上午
 */
public class ApiOptionIsReadonlyException extends BadRequestException {
    public ApiOptionIsReadonlyException(String id) {
        super(id);
    }
}
