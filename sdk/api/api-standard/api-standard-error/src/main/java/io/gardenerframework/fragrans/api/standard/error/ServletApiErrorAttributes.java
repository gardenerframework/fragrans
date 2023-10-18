package io.gardenerframework.fragrans.api.standard.error;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.fragrans.api.standard.error.configuration.ApiStandardErrorComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 继承{@link DefaultErrorAttributes}，重写{@link DefaultErrorAttributes#getErrorAttributes(WebRequest, boolean)}方法<br>
 * 在该方法后将转换完的错误数据转为接口规范格式<br>
 *
 * @author zhanghan
 * @date 2020-11-13 15:42
 * @since 1.0.0
 */
@ApiStandardErrorComponent
@Primary
@RequiredArgsConstructor
public class ServletApiErrorAttributes extends DefaultErrorAttributes implements InitializingBean {
    private final AntPathMatcher matcher = new AntPathMatcher();
    private final ObjectMapper mapper;
    private final ApiErrorFactory apiErrorFactory;
    private final Collection<ServletApiErrorAttributesConfigurer> configurers;
    @Getter
    private final Collection<String> ignoringUrlPatterns = new ArrayList<>();

    /**
     * 转换为符合接口规范的错误数据
     *
     * @param webRequest {@link WebRequest} 请求容器，可以被理解为{@link RequestAttributes}
     * @param options    输出信息的选项，包含是否输出stack trace
     * @return 错误数据，会被输出为json
     */
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        //首先获取默认的spring的处理
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        if (!CollectionUtils.isEmpty(ignoringUrlPatterns)) {
            for (String pattern : ignoringUrlPatterns) {
                if (matcher.match(pattern, String.valueOf(webRequest.getAttribute("javax.servlet.forward.request_uri", RequestAttributes.SCOPE_REQUEST)))) {
                    return errorAttributes;
                }
            }
        }
        return mapper.convertValue(
                apiErrorFactory.createApiError(errorAttributes, getError(webRequest), LocaleContextHolder.getLocale()),
                new TypeReference<Map<String, Object>>() {
                }
        );
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!CollectionUtils.isEmpty(this.configurers)) {
            this.configurers.forEach(
                    servletApiErrorAttributesConfigurer -> servletApiErrorAttributesConfigurer.accept(this)
            );
        }
    }
}
