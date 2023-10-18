package io.gardenerframework.fragrans.api.group.support;

import io.gardenerframework.fragrans.api.group.configuration.ApiGroupComponent;
import io.gardenerframework.fragrans.api.group.policy.ApiGroupContextPathPolicy;
import io.gardenerframework.fragrans.api.group.registry.ApiGroupRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author zhanghan30
 * @date 2022/8/18 2:52 上午
 */
@ApiGroupComponent
public class ApiGroupContextPathPolicySupport implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RequestMappingHandlerMapping) {
            setContextPath((RequestMappingHandlerMapping) bean);
        }
        return bean;
    }

    private void setContextPath(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        ApiGroupRegistry registry = this.applicationContext.getBean(ApiGroupRegistry.class);
        //获取当前策略对应的所有成员类
        Collection<Class<?>> member = registry.getMember(ApiGroupContextPathPolicy.class);
        Map<String, Predicate<Class<?>>> pathPrefixes = new ConcurrentHashMap<>();
        if (!CollectionUtils.isEmpty(member)) {
            member.forEach(
                    clazz -> {
                        //获取策略
                        ApiGroupContextPathPolicy policy = registry.getPolicy(clazz, ApiGroupContextPathPolicy.class);
                        String prefix = policy.getContextPath();
                        CompositePredicate predicate = (CompositePredicate) pathPrefixes.get(prefix);
                        if (predicate == null) {
                            pathPrefixes.put(prefix, new CompositePredicate());
                            predicate = (CompositePredicate) pathPrefixes.get(prefix);
                        }
                        predicate.addClass(clazz);
                    }
            );
        }
        requestMappingHandlerMapping.setPathPrefixes(pathPrefixes);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private class CompositePredicate implements Predicate<Class<?>> {
        private Collection<Class<?>> classes = new HashSet<>();

        public void addClass(Class<?> clazz) {
            classes.add(clazz);
        }

        @Override
        public boolean test(Class<?> aClass) {
            return classes.contains(aClass);
        }
    }
}
