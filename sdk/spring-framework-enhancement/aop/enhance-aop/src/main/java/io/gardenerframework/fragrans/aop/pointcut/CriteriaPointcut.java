package io.gardenerframework.fragrans.aop.pointcut;

import io.gardenerframework.fragrans.pattern.criteria.schema.object.JavaObjectCriteria;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

@SuperBuilder
@Getter
@Setter
public class CriteriaPointcut implements Pointcut {
    @Nullable
    private JavaObjectCriteria<Class<?>> classCriteria;
    @Nullable
    private JavaObjectCriteria<Method> methodCriteria;

    @Override
    public ClassFilter getClassFilter() {
        return clazz -> classCriteria == null || classCriteria.meetCriteria(clazz);
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return methodCriteria == null || methodCriteria.meetCriteria(method);
            }

            @Override
            public boolean isRuntime() {
                return false;
            }

            @Override
            public boolean matches(Method method, Class<?> targetClass, Object... args) {
                return matches(method, targetClass);
            }
        };
    }
}
