package com.jdcloud.gardener.fragrans.api.idempotent.exception;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.BadRequestException;
import com.jdcloud.gardener.fragrans.messages.support.MessageArgumentsSupplier;
import lombok.Getter;

/**
 * @author zhanghan30
 * @date 2022/2/24 3:12 下午
 */
@Getter
public class DuplicateHttpRequestException extends BadRequestException implements MessageArgumentsSupplier {
    private final String factor;

    public DuplicateHttpRequestException(String factor) {
        this.factor = factor;
    }

    @Override
    public Object[] getMessageArguments() {
        return new String[]{factor};
    }
}
