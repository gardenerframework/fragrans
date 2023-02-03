package io.gardenerframework.fragrans.api.security.automation.data.advice;

import io.gardenerframework.fragrans.aop.interceptor.EnhanceMethodInterceptor;
import io.gardenerframework.fragrans.api.security.automation.data.annotation.InjectOperator;
import io.gardenerframework.fragrans.api.security.operator.schema.OperatorBrief;
import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author zhanghan30
 * @date 2023/2/3 12:37
 */
@AllArgsConstructor
public class OperatorInjector implements EnhanceMethodInterceptor {
    private final OperatorBrief operatorBrief;

    @Override
    public Pointcut getPointcut() {
        return new Pointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return new ClassFilter() {
                    @Override
                    public boolean matches(Class<?> clazz) {
                        return true;
                    }
                };
            }

            @Override
            public MethodMatcher getMethodMatcher() {
                return new MethodMatcher() {
                    @Override
                    public boolean matches(Method method, Class<?> targetClass) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes != null) {
                            //分析类型
                            for (Class<?> type : parameterTypes) {
                                //以下几个trait包含在参数类型中
                                if (SecurityTraits.AuditingTraits.IdentifierTraits
                                        .Operator.class.isAssignableFrom(type)
                                        ||
                                        SecurityTraits.AuditingTraits.IdentifierTraits
                                                .Creator.class.isAssignableFrom(type)
                                        ||
                                        SecurityTraits.AuditingTraits.IdentifierTraits
                                                .Updater.class.isAssignableFrom(type)
                                        || SecurityTraits.AuditingTraits.IdentifierTraits
                                        .Deleter.class.isAssignableFrom(type)) {
                                    return true;
                                }
                            }
                            //类型不匹配
                            //分析注解
                            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                            int parameterIndex = 0;
                            for (Annotation[] annotations : parameterAnnotations) {
                                for (Annotation annotation : annotations) {
                                    //参数是要求注入操作人的注解
                                    if (annotation instanceof InjectOperator &&
                                            //注入的参数类型必须是string
                                            String.class.isAssignableFrom(parameterTypes[parameterIndex])) {
                                        return true;
                                    }
                                }
                                parameterIndex++;
                            }
                        }
                        //没有参数符合类型定义，返回
                        return false;
                    }

                    @Override
                    public boolean isRuntime() {
                        return false;
                    }

                    @Override
                    public boolean matches(Method method, Class<?> targetClass, Object... args) {
                        return false;
                    }
                };
            }
        };
    }

    @Override
    public void before(Object target, Method method, Object[] arguments) throws Exception {
        //当前处于http请求处理过程中且实际有操作人被注入进来
        if (RequestContextHolder.getRequestAttributes() != null && StringUtils.hasText(operatorBrief.getUserId())) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < arguments.length; i++) {
                //按类型注入
                if (arguments[i] instanceof SecurityTraits.AuditingTraits.IdentifierTraits.Operator
                        && !StringUtils.hasText(((SecurityTraits.AuditingTraits.IdentifierTraits.Operator) arguments[i]).getOperator())) {
                    //开发人员自己没有写入操作人
                    ((SecurityTraits.AuditingTraits.IdentifierTraits.Operator) arguments[i]).setOperator(operatorBrief.getUserId());
                }
                if (arguments[i] instanceof SecurityTraits.AuditingTraits.IdentifierTraits.Creator
                        && !StringUtils.hasText(((SecurityTraits.AuditingTraits.IdentifierTraits.Creator) arguments[i]).getCreator())) {
                    ((SecurityTraits.AuditingTraits.IdentifierTraits.Creator) arguments[i]).setCreator(operatorBrief.getUserId());
                }
                if (arguments[i] instanceof SecurityTraits.AuditingTraits.IdentifierTraits.Updater
                        && !StringUtils.hasText(((SecurityTraits.AuditingTraits.IdentifierTraits.Updater) arguments[i]).getUpdater())) {
                    ((SecurityTraits.AuditingTraits.IdentifierTraits.Updater) arguments[i]).setUpdater(operatorBrief.getUserId());
                }
                if (arguments[i] instanceof SecurityTraits.AuditingTraits.IdentifierTraits.Deleter
                        && !StringUtils.hasText(((SecurityTraits.AuditingTraits.IdentifierTraits.Deleter) arguments[i]).getDeleter())) {
                    ((SecurityTraits.AuditingTraits.IdentifierTraits.Deleter) arguments[i]).setDeleter(operatorBrief.getUserId());
                }
                //按注解注入
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof InjectOperator
                            && String.class.isAssignableFrom(parameterTypes[i])
                            && !StringUtils.hasText((CharSequence) arguments[i])) {
                        arguments[i] = operatorBrief.getUserId();
                    }
                }
            }
        }
    }
}
