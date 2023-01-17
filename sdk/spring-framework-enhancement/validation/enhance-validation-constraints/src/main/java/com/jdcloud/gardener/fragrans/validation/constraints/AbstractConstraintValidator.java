package com.jdcloud.gardener.fragrans.validation.constraints;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/6/10 4:55 下午
 */
public abstract class AbstractConstraintValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {
    private final Class<A> annotationClass;

    protected AbstractConstraintValidator() {
        this.annotationClass = findAnnotationClass(this.getClass());
    }

    /**
     * 子类实现具体的验证方法
     *
     * @param value   值
     * @param context 上下文
     * @param data    数据字段，用于实现类自己存储一些验证过程中的数据，这些数据的设计初衷是给构建错误消息时使用的
     * @return 是否合法
     */
    protected abstract boolean validate(T value, ConstraintValidatorContext context, Map<String, Object> data);

    /**
     * 构建参数错误消息，其实主要是往里面填参数
     *
     * @param value 值
     * @param data  自己在验证时存的数据
     */
    @Nullable
    protected Map<String, Object> getMessageParameters(T value, Map<String, Object> data) {
        return null;
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        Map<String, Object> data = new LinkedHashMap<>();
        boolean isValid = validate(value, context, data);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            ConstraintValidatorContext.ConstraintViolationBuilder builder = context.buildConstraintViolationWithTemplate(String.format("{%s}",
                    ClassUtils.getUserClass(annotationClass).getCanonicalName()
            ));
            if (context instanceof HibernateConstraintValidatorContext) {
                Map<String, Object> messageParameters = getMessageParameters(value, data);
                if (!CollectionUtils.isEmpty(messageParameters)) {
                    HibernateConstraintValidatorContext unwrap = context.unwrap(HibernateConstraintValidatorContext.class);
                    messageParameters.forEach(
                            unwrap::addMessageParameter
                    );
                }
            }
            builder.addConstraintViolation();
        }
        return isValid;
    }

    @SuppressWarnings("unchecked")
    private Class<A> findAnnotationClass(Class<?> clazz) {
        Assert.isTrue(AbstractConstraintValidator.class.isAssignableFrom(clazz), clazz + " must be subclass of AbstractConstraintValidator");
        {
            Type superclass = clazz.getGenericSuperclass();
            if (superclass instanceof ParameterizedType) {
                return (Class<A>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
            } else {
                Assert.isTrue(superclass instanceof Class, superclass + " must be type of Class");
                return findAnnotationClass((Class<?>) superclass);
            }
        }
    }
}