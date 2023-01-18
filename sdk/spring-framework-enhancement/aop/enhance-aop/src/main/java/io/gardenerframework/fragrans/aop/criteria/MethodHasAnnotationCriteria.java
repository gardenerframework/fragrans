package io.gardenerframework.fragrans.aop.criteria;

import io.gardenerframework.fragrans.pattern.criteria.schema.object.JavaObjectCriteria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class MethodHasAnnotationCriteria implements JavaObjectCriteria<Method> {
    /**
     * 要匹配的注解
     */
    @NonNull
    private Class<? extends Annotation> annotation;

    @Override
    public boolean meetCriteria(Method object) {
        return AnnotationUtils.findAnnotation(object, annotation) != null;
    }
}
