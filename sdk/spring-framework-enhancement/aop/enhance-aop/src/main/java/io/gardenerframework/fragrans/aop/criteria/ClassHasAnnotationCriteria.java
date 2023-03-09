package io.gardenerframework.fragrans.aop.criteria;

import io.gardenerframework.fragrans.pattern.criteria.schema.object.JavaObjectCriteria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ClassHasAnnotationCriteria implements JavaObjectCriteria<Class<?>> {
    /**
     * 要匹配的注解
     */
    @NonNull
    private Class<? extends Annotation> annotation;

    @Override
    public boolean meetCriteria(Class<?> object) {
        return AnnotationUtils.findAnnotation(object, annotation) != null;
    }
}
