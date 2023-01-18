package io.gardenerframework.fragrans.aop.criteria;

import io.gardenerframework.fragrans.pattern.criteria.schema.object.JavaObjectCriteria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.ClassUtils;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ClassMatchesCriteria implements JavaObjectCriteria<Class<?>> {
    /**
     * 要匹配的类型
     */
    @NonNull
    private Class<?> superClass;

    @Override
    public boolean meetCriteria(Class<?> object) {
        return superClass.equals(ClassUtils.getUserClass(object));
    }
}
