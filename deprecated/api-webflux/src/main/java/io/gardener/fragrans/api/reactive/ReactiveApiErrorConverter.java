package com.jdcloud.gardener.fragrans.api.reactive;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcloud.gardener.fragrans.api.ApiErrorFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

/**
 * 继承{@link DefaultErrorAttributes}
 * 将响应出处规整化为符合规范标准要求的格式
 *
 * @author zhanghan
 * @date 2020-11-13 22:44
 * @since 1.0.0
 */
public class ReactiveApiErrorConverter extends DefaultErrorAttributes {

    private final ApiErrorFactory apiErrorFactory;
    private final ObjectMapper mapper;

    public ReactiveApiErrorConverter(ApiErrorFactory apiErrorFactory, ObjectMapper mapper) {
        this.apiErrorFactory = apiErrorFactory;
        this.mapper = mapper;
    }

    /**
     * @param request 请求
     * @param options 包含的信息的选项
     * @return 错误数据
     */
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        return mapper.convertValue(
                apiErrorFactory.createApiError(errorAttributes, getError(request), request.exchange().getLocaleContext().getLocale()),
                new TypeReference<Map<String, Object>>() {
                }
        );
    }
}
