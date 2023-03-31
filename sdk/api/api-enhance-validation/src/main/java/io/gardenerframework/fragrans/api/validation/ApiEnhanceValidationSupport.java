package io.gardenerframework.fragrans.api.validation;

import io.gardenerframework.fragrans.api.advice.engine.EndpointHandlerMethodAdvice;
import io.gardenerframework.fragrans.api.standard.error.exception.client.BadRequestArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.*;
import javax.validation.executable.ExecutableValidator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author zhanghan
 * @date 2021/8/25 15:14
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiEnhanceValidationSupport implements EndpointHandlerMethodAdvice {
    private final Validator validator;
    /**
     * 方法签名的缓存
     */
    private final Map<String, Boolean> methodCache = new ConcurrentHashMap<>();

    public ApiEnhanceValidationSupport(Validator validator) {
        //从spring的代码上抄的
        // Unwrap to the native Validator with forExecutables support
        if (validator instanceof LocalValidatorFactoryBean) {
            this.validator = ((LocalValidatorFactoryBean) validator).getValidator();
        } else if (validator instanceof SpringValidatorAdapter) {
            this.validator = validator.unwrap(Validator.class);
        } else {
            this.validator = validator;
        }
    }

    private String getSignatureCode(Method method) {
        List<String> elements = new LinkedList<>();
        elements.add(Modifier.toString(method.getModifiers()));
        elements.add(method.getReturnType().getCanonicalName());
        elements.add(method.getDeclaringClass().getCanonicalName());
        elements.add(method.getName());
        elements.add("(");
        elements.add(Arrays.stream(method.getParameterTypes()).map(
                Class::getCanonicalName
        ).collect(Collectors.joining(",")));
        elements.add(")");
        return String.join(" ", elements);
    }

    /**
     * 分析当前方法是否应该进行参数校验
     * <p>
     * 方式是找到任何一个参数同时有@PathVariable/@RequestParam和@Valid注解
     * <p>
     * 或者是当前方法具备Http Get方法，且参数没有@RequestBody、@PathVariable、@RequestParam但有@Valid注解
     *
     * @param method 方法签名
     */
    private boolean analyzeSignature(Method method) {
        Annotation[][] allParameterAnnotations = method.getParameterAnnotations();
        boolean isGetMappingEquivalentPresent = false;
        RequestMapping requestMappingAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        if (requestMappingAnnotation != null) {
            isGetMappingEquivalentPresent = Arrays.asList(requestMappingAnnotation.method()).contains(RequestMethod.GET);
        }
        boolean shouldValidateArguments = false;
        for (Annotation[] singleParameterAnnotations : allParameterAnnotations) {
            //遍历当前参数的所有注解
            boolean isPathVariableOrRequestParamPresent = false;
            boolean isValidPresent = false;
            boolean isConstraintAnnotation = false;
            for (Annotation annotation : singleParameterAnnotations) {
                if (annotation instanceof RequestParam || annotation instanceof PathVariable) {
                    isPathVariableOrRequestParamPresent = true;
                }
                if (annotation instanceof Valid) {
                    isValidPresent = true;
                }
                if (annotation.annotationType().isAnnotationPresent(Constraint.class)) {
                    isConstraintAnnotation = true;
                }
            }
            //符合2个条件则当前方法就应当被验证
            if ((isPathVariableOrRequestParamPresent || isGetMappingEquivalentPresent || isConstraintAnnotation) && isValidPresent) {
                shouldValidateArguments = true;
                break;
            }
        }
        methodCache.put(getSignatureCode(method), shouldValidateArguments);
        return shouldValidateArguments;
    }

    /**
     * 进行实际的验证
     *
     * @param target    验证目标
     * @param method    方法
     * @param arguments 参数
     * @return 验证结果
     */
    private Set<ConstraintViolation<Object>> doValidate(Object target, Method method, Object[] arguments) {
        ExecutableValidator executableValidator = this.validator.forExecutables();
        Set<ConstraintViolation<Object>> result;
        //也是抄的
        try {
            result = executableValidator.validateParameters(target, method, arguments);
        } catch (IllegalArgumentException ex) {
            // Probably a generic type mismatch between interface and impl as reported in SPR-12237 / HV-1011
            // Let's try to find the bridged method on the implementation class...
            method = BridgeMethodResolver.findBridgedMethod(
                    ClassUtils.getMostSpecificMethod(method, target.getClass()));
            result = executableValidator.validateParameters(target, method, arguments);
        }
        return result;
    }

    /**
     * 执行参数校验
     *
     * @param target    目标对象
     * @param method    方法签名
     * @param arguments 参数
     */
    private void validateArguments(Object target, Method method, Object[] arguments) {
        Set<ConstraintViolation<Object>> constraintViolations = null;
        try {
            //方法的宦存键
            String signatureCode = getSignatureCode(method);
            Boolean shouldValidate = methodCache.get(signatureCode);
            if (shouldValidate == null) {
                shouldValidate = analyzeSignature(method);
            }
            if (!shouldValidate) {
                if (log.isDebugEnabled()) {
                    log.debug("{} does not need to validate", signatureCode);
                }
                return;
            }
            constraintViolations = doValidate(target, method, arguments);
        } catch (Throwable throwable) {
            log.error("Exception caught:", throwable);
            throw new IllegalStateException(throwable);
        }
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            Map<String, Object> details = new HashMap<>(constraintViolations.size());
            constraintViolations.forEach(
                    constraintViolation -> {
                        Path propertyPath = constraintViolation.getPropertyPath();

                        propertyPath.forEach(
                                node -> {
                                    if (ElementKind.PARAMETER.equals(node.getKind())) {
                                        details.put(node.getName(), constraintViolation.getMessage());
                                    }
                                }
                        );
                    }
            );
            throw new BadRequestArgumentException(details);
        }
    }

    @Override
    public void before(Object target, Method method, Object[] arguments) throws Exception {
        validateArguments(target, method, arguments);
    }
}
