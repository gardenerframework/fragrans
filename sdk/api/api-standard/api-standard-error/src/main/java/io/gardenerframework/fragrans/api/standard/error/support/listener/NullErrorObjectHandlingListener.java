package io.gardenerframework.fragrans.api.standard.error.support.listener;

import io.gardenerframework.fragrans.api.standard.error.exception.HttpStatusRepresentative;
import io.gardenerframework.fragrans.api.standard.error.exception.client.BadRequestException;
import io.gardenerframework.fragrans.api.standard.error.exception.server.InternalServerErrorException;
import io.gardenerframework.fragrans.api.standard.error.support.DefaultApiErrorFactory;
import io.gardenerframework.fragrans.api.standard.error.support.event.InitializingApiErrorPropertiesEvent;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理 sendError 这种没有抛出异常场景
 *
 * @author zhanghan30
 * @date 2022/5/9 6:12 下午
 */
@Component
@AllArgsConstructor
@ConditionalOnBean(DefaultApiErrorFactory.class)
@Order(0)
public class NullErrorObjectHandlingListener implements ApplicationListener<InitializingApiErrorPropertiesEvent> {
    private static final Map<Integer, Class<? extends RuntimeException>> HTTP_STATUS_REPRESENTATIVE_REGISTRY = new ConcurrentHashMap<>();

    static {
        initializeStatusMap();
    }

    private final EnhancedMessageSource messageSource;

    private static void initializeStatusMap() {
        String[] packages = new String[]{
                ClassUtils.getPackageName(BadRequestException.class),
                ClassUtils.getPackageName(InternalServerErrorException.class)
        };
        for (String _package : packages) {
            Set<Class<? extends RuntimeException>> classes = new HashSet<>(new Reflections(_package, new SubTypesScanner(false))
                    .getSubTypesOf(RuntimeException.class));
            for (Class<? extends RuntimeException> clazz : classes) {
                if (clazz.getAnnotation(HttpStatusRepresentative.class) == null) {
                    continue;
                }
                ResponseStatus annotation = AnnotationUtils.getAnnotation(clazz, ResponseStatus.class);
                Assert.notNull(annotation, "no @ResponseStatus annotation found " + clazz);
                HttpStatus value = annotation.value();
                HTTP_STATUS_REPRESENTATIVE_REGISTRY.put(value.value(), clazz);
            }
        }
    }

    @Override
    public void onApplicationEvent(InitializingApiErrorPropertiesEvent event) {
        if (event.getError() == null) {
            //从类型映射中读取出来应当对应的类型
            Class<? extends RuntimeException> clazz = HTTP_STATUS_REPRESENTATIVE_REGISTRY.get(event.getApiError().getStatus());
            if (clazz != null) {
                //使用映射的类名作为错误编码
                event.getApiError().setError(clazz.getCanonicalName());
                //使用映射的类名作为错误信息
                event.getApiError().setMessage(messageSource.getMessage(clazz, event.getLocale()));
            }
        }
    }
}
