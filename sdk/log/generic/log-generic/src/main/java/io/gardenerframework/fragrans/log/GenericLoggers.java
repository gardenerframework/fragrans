package io.gardenerframework.fragrans.log;

/**
 * @author zhanghan30
 * @date 2022/6/16 12:35 下午
 */
public abstract class GenericLoggers {
    private static final GenericBasicLogger basicLogger = new GenericBasicLogger();
    private static final GenericOperationLogger operationLogger = new GenericOperationLogger();


    public static GenericBasicLogger basicLogger() {
        return basicLogger;
    }

    public static GenericOperationLogger operationLogger() {

        return operationLogger;
    }
}
