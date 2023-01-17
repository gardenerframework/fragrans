package com.jdcloud.gardener.fragrans.api.idempotent.core;

import com.jdcloud.gardener.fragrans.api.advice.EndpointHandlerMethodAdvice;
import com.jdcloud.gardener.fragrans.api.idempotent.engine.annotation.IdempotentApi;
import com.jdcloud.gardener.fragrans.api.idempotent.engine.factor.IdempotentFactorSupplier;
import com.jdcloud.gardener.fragrans.api.idempotent.exception.DuplicateHttpRequestException;
import com.jdcloud.gardener.fragrans.api.idempotent.exception.IdempotentFactorNotFoundException;
import com.jdcloud.gardener.fragrans.api.idempotent.log.payload.HttpRequestPayload;
import com.jdcloud.gardener.fragrans.api.idempotent.log.target.HttpRequestTarget;
import com.jdcloud.gardener.fragrans.api.idempotent.log.target.IdempotentFactorTarget;
import com.jdcloud.gardener.fragrans.log.InvalidTargetLogWriter;
import com.jdcloud.gardener.fragrans.log.OperationLogWriter;
import com.jdcloud.gardener.fragrans.log.schema.operation.action.Process;
import com.jdcloud.gardener.fragrans.log.schema.operation.state.Failed;
import com.jdcloud.gardener.fragrans.log.schema.reason.SubjectCompositeReason;
import com.jdcloud.gardener.fragrans.log.schema.reason.TargetAlreadyExisted;
import com.jdcloud.gardener.fragrans.log.schema.reason.TargetNotFound;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.time.Duration;

/**
 * 为{@link IdempotentApi} 注解实现功能
 *
 * @author zhanghan30
 * @date 2022/2/24 2:32 下午
 */
@AllArgsConstructor
@Slf4j
public abstract class AbstractIdempotentApiProtector implements EndpointHandlerMethodAdvice {
    private final IdempotentFactorSupplier idempotentFactorSupplier;
    private final IdempotentFactorStore idempotentFactorStore;

    /**
     * 给出当前调用的http请求
     *
     * @return 请求
     */
    protected abstract HttpRequest getHttpRequest();

    /**
     * 给出当前调用的 ttl
     *
     * @param request 本次调用
     * @return ttl
     */
    protected abstract Duration getIdempotentFactorTtl(HttpRequest request);

    /**
     * 判断是否应当对目标进行保护
     *
     * @param request         http 请求
     * @param target          防护目标对象
     * @param methodSignature 被防护的方法
     * @return 是否防护
     */
    private boolean shouldProtectTarget(HttpRequest request, Object target, MethodSignature methodSignature) {
        IdempotentApi annotation = AnnotationUtils.getAnnotation(target.getClass(), IdempotentApi.class);
        if (annotation != null) {
            //注解在类上，查看是否有要排除的http方法
            HttpMethod[] excludedHttpMethods = annotation.excludeMethods();
            if (excludedHttpMethods != null && excludedHttpMethods.length > 0) {
                HttpMethod requestMethod = request.getMethod();
                return !ArrayUtils.toUnmodifiableList(excludedHttpMethods).contains(requestMethod);
            } else {
                //没有要排除的http请求方法，全类都需要进行保护
                return true;
            }
        } else {
            //当前方法上有注解
            return AnnotationUtils.getAnnotation(methodSignature.getMethod(), IdempotentApi.class) != null;
        }
    }

    @Override
    public void before(Object target, MethodSignature methodSignature, Object[] arguments) throws Exception {
        HttpRequest request = getHttpRequest();
        if (!shouldProtectTarget(request, target, methodSignature)) {
            return;
        }
        String factor = idempotentFactorSupplier.getIdempotentFactor(request);
        if (!StringUtils.hasText(factor)) {
            //没有提交幂等因子
            InvalidTargetLogWriter.writeRawErrorLog(
                    log,
                    IdempotentFactorTarget.class,
                    new TargetNotFound(),
                    new HttpRequestPayload(request.getMethod(), request.getURI().getPath()),
                    null
            );
            throw new IdempotentFactorNotFoundException();
        } else {
            if (!idempotentFactorStore.saveIfAbsent(request.getMethod(), request.getURI().getPath(), factor, getIdempotentFactorTtl(request))) {
                //当前是在重复提交
                OperationLogWriter.writeGenericRawErrorLog(
                        log,
                        new Failed(
                                new SubjectCompositeReason(
                                        new IdempotentFactorTarget(),
                                        new TargetAlreadyExisted()
                                )
                        ),
                        new Process(),
                        HttpRequestTarget.class,
                        null,
                        new HttpRequestPayload(request.getMethod(), request.getURI().getPath()),
                        null
                );
                throw new DuplicateHttpRequestException(factor);
            }
        }
    }
}
