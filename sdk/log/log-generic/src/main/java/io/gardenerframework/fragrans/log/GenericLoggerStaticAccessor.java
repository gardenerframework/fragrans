package io.gardenerframework.fragrans.log;

import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/6/16 12:35 下午
 */
@Component
public class GenericLoggerStaticAccessor {
    private static final GenericBasicLogger basicLogger = new GenericBasicLogger();
    private static final GenericOperationLogger operationLogger = new GenericOperationLogger();


    public static GenericBasicLogger basicLogger() {
        return basicLogger;
    }

    public static GenericOperationLogger operationLogger() {

        return operationLogger;
    }
}
