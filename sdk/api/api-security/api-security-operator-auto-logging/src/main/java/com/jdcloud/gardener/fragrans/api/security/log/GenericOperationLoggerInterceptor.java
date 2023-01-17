package com.jdcloud.gardener.fragrans.api.security.log;

import com.jdcloud.gardener.fragrans.api.security.log.schema.Subject;
import com.jdcloud.gardener.fragrans.api.security.schema.Operator;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.OperatorDetail;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/6/14 2:23 下午
 */
@Aspect
@Component
@AllArgsConstructor
public class GenericOperationLoggerInterceptor {
    private final Operator operator;
    private final Collection<String> methodNames = Arrays.asList("debug", "info", "warn", "error");

    /**
     * 拦截日志类的执行
     *
     * @param proceedingJoinPoint 切点
     * @return 原来的返回值
     * @throws Throwable 遇到问题抛出的异常
     */
    @Around("target(com.jdcloud.gardener.fragrans.log.GenericOperationLogger)")
    public Object intercept(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] arguments = proceedingJoinPoint.getArgs();
        Signature signature = proceedingJoinPoint.getSignature();
        if (signature instanceof MethodSignature) {
            Method method = ((MethodSignature) signature).getMethod();
            if (methodNames.contains(method.getName())) {
                for (int i = 0; i < arguments.length; i++) {
                    //如果参数为null或者不是目标类型都无视掉
                    if (arguments[i] instanceof GenericOperationLogContent) {
                        GenericOperationLogContent content = (GenericOperationLogContent) arguments[i];
                        OperatorDetail operatorDetail = content.getOperator();
                        try {
                            //操作人没有赋值而且当前也确实获得了操作人的一些信息
                            if (operatorDetail == null && (
                                    StringUtils.hasText(this.operator.getUserId())
                                            || StringUtils.hasText(this.operator.getClientId())
                            )) {
                                //覆盖原始参数
                                arguments[i] = copyLogContent(content);
                            }
                        } catch (Exception exception) {
                            //觉得不应当由于日志的异常导致程序失败
                        }
                    }
                }
            }
        }
        return proceedingJoinPoint.proceed(arguments);
    }

    /**
     * 复制一份日志内容
     *
     * @param content 日志
     * @return 日志内容
     */
    private GenericOperationLogContent copyLogContent(GenericOperationLogContent content) {
        return GenericOperationLogContent.builder()
                .what(content.getWhat())
                .operation(content.getOperation())
                .state(content.getState())
                .detail(content.getDetail())
                .operator(new OperatorDetail(
                        new Subject(operator.getUserId()),
                        new Subject(operator.getClientId())
                )).build();
    }
}
