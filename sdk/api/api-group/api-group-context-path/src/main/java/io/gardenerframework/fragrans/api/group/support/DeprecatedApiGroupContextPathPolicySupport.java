package io.gardenerframework.fragrans.api.group.support;

import io.gardenerframework.fragrans.api.group.policy.ApiGroupContextPathPolicy;
import io.gardenerframework.fragrans.api.group.registry.ApiGroupRegistry;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Update;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ZhangHan
 * @date 2022/5/10 19:31
 */
@AllArgsConstructor
@Slf4j
@Deprecated
public class DeprecatedApiGroupContextPathPolicySupport implements InitializingBean {
    /**
     * 注册表
     */
    private final ApiGroupRegistry registry;
    /**
     * 所有controller方法的映射存储
     */
    private final RequestMappingHandlerMapping handlerMapping;
    /**
     * 记日志的
     */
    private final GenericOperationLogger operationLogger;

    @Override
    public void afterPropertiesSet() throws Exception {
        //获取所有映射的方法
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        handlerMethods.forEach(
                (mapping, handlerMethod) -> {
                    //获取方法的类
                    Class<?> declaringClass = handlerMethod.getMethod().getDeclaringClass();
                    ApiGroupContextPathPolicy apiGroupContextPathPolicy = registry.getPolicy(declaringClass, ApiGroupContextPathPolicy.class);
                    //当前接口没有进入编组或没有路径设置
                    if (apiGroupContextPathPolicy == null || !StringUtils.hasText(apiGroupContextPathPolicy.getContextPath())) {
                        return;
                    }
                    //生成一个新的映射信息实例
                    RequestMappingInfo requestMappingInfo = mapping.addCustomCondition(null);
                    //获取原来的所有路径
                    Set<String> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
                    //开始替换路径
                    Set<String> patternsWithContextPath = new HashSet<>(patterns.size());
                    patterns.forEach(
                            pattern -> patternsWithContextPath.add(
                                    String.format("%s%s", apiGroupContextPathPolicy.getContextPath(), pattern)
                            )
                    );
                    patterns.clear();
                    patterns.addAll(patternsWithContextPath);
                    //取消注册
                    handlerMapping.unregisterMapping(mapping);
                    //注册新的
                    handlerMapping.registerMapping(requestMappingInfo, handlerMethod.getBean(), handlerMethod.getMethod());
                    operationLogger.info(
                            log,
                            GenericOperationLogContent.builder()
                                    .what(RequestMappingInfo.class)
                                    .operation(new Update())
                                    .state(new Done())
                                    .detail(new Detail() {
                                        private final RequestMappingInfo mappingInfo = requestMappingInfo;
                                        private final Method method = handlerMethod.getMethod();
                                    })
                                    .build(),
                            null
                    );
                }
        );
    }
}
