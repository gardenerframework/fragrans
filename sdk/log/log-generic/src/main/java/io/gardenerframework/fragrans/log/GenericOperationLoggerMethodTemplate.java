package io.gardenerframework.fragrans.log;

import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;

/**
 * @author zhanghan30
 * @date 2022/6/17 1:02 下午
 */
@FunctionalInterface
public interface GenericOperationLoggerMethodTemplate extends GenericLoggerMethodTemplate<GenericOperationLogContent> {

}
