package com.jdcloud.gardener.fragrans.api.options.test;

import com.jdcloud.gardener.fragrans.api.options.schema.ApiOption;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/5/10 2:19 上午
 */
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ApiOption(readonly = false)
public class ConditionalOptions {
}
