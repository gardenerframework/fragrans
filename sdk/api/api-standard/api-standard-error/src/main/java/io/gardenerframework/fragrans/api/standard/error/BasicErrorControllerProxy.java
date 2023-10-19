package io.gardenerframework.fragrans.api.standard.error;

import io.gardenerframework.fragrans.api.standard.error.configuration.ApiStandardErrorComponent;
import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.common.schema.reason.ExceptionCaught;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * {@link BasicErrorController#error(HttpServletRequest)}方法实现有逻辑问题
 * 它先获取状态码后获取错误属性，因此由本代理拦截方法执行，由错误属性的状态码覆盖之前的实现
 *
 * @author zhanghan
 * @date 2020-11-13 16:32
 * @since 1.0.0
 */
@Aspect
@Slf4j
@ApiStandardErrorComponent
@AllArgsConstructor
public class BasicErrorControllerProxy {
    private final GenericBasicLogger basicLogger = GenericLoggers.basicLogger();

    /**
     * 拦截{@link BasicErrorController#error(HttpServletRequest)}方法的执行，将状态码修正为预期值
     *
     * @param proceedingJoinPoint 切点
     * @return 错误数据
     * @throws Throwable 异常
     */
    @SuppressWarnings("unchecked")
    @Around("execution(* org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController.error(..))")
    public Object proxyErrorMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ResponseEntity<Map<String, Object>> entity = (ResponseEntity<Map<String, Object>>) proceedingJoinPoint.proceed();
        /*
        这个不为空是敢断言的，因为BasicErrorController.error方法返回的是一个new出来的对象
         */
        Assert.notNull(entity, "entity must not be null");
        Map<String, Object> errorAttributes = entity.getBody();
        /*
         BasicController#error 是有坑的，204的状态码没有body
         */
        if (errorAttributes != null) {
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            try {
                httpStatus = HttpStatus.valueOf((int) errorAttributes.get("status"));
            } catch (IllegalArgumentException exception) {
                basicLogger.debug(
                        log,
                        GenericBasicLogContent.builder().
                                what(BasicErrorControllerProxy.class)
                                .how(new ExceptionCaught())
                                .detail(new Detail() {
                                    private final Object status = entity.getBody().get("status");
                                }).build(),
                        exception);
            }
            return new ResponseEntity<>(entity.getBody(), httpStatus);
        } else {
            return entity;
        }
    }

    /**
     * {@link BasicErrorController}的errorHtml方法实现也有逻辑问题，它先获取状态码后获取错误属性，因此在此拦截方法执行，由错误属性的状态码覆盖之前的实现
     *
     * @param proceedingJoinPoint 切点
     * @return 错误页面
     * @throws Throwable 异常
     */
    @SuppressWarnings("unchekced")
    @Around("execution(org.springframework.web.servlet.ModelAndView org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController.errorHtml(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse))")
    public Object proxyErrorHtmlMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        /*
        这个断言是可以下的，因为BasicErrorController的errorHtml必然总是会返回一个ModelAndView对象
         */
        Assert.isTrue(result instanceof ModelAndView, "result must be a ModelAndView instance");
        ModelAndView mav = (ModelAndView) result;
        Object[] args = proceedingJoinPoint.getArgs();
        /*
        这个断言可以查看切面要求拦截的方法声明
         */
        Assert.isTrue(args[1] instanceof HttpServletResponse, "arg[1] must be HttpServletResponse instance");
        /*
         mav.getModel()不会空指针，可以看下它的实现，如果没有model就new一个
         */
        ((HttpServletResponse) args[1]).setStatus((int) mav.getModel().get("status"));
        return mav;
    }
}
