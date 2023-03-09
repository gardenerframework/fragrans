package io.gardenerframework.fragrans.data.unique;

/**
 * 为雪花id生成主机id
 *
 * @author zhanghan
 * @date 2021/8/19 15:38
 * @since 1.0.0
 */
@FunctionalInterface
public interface HostIdGenerator {
    /**
     * 生成id
     *
     * @return id
     */
    String getHostId();
}
