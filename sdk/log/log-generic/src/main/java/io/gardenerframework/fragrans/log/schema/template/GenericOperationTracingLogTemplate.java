package io.gardenerframework.fragrans.log.schema.template;

/**
 * @author ZhangHan
 * @date 2022/6/9 1:19
 */
public class GenericOperationTracingLogTemplate extends GenericOperationLogTemplate {
    @Override
    public String toString() {
        return super.toString() + ", 操作方: {}";
    }
}
