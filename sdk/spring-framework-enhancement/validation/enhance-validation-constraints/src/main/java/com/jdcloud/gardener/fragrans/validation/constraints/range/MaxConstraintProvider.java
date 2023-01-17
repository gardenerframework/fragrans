package com.jdcloud.gardener.fragrans.validation.constraints.range;

/**
 * @author zhanghan30
 * @date 2022/6/13 7:28 下午
 */
@FunctionalInterface
public interface MaxConstraintProvider {
    /**
     * 给出最大值
     *
     * @return 最大值
     */
    Number getMax();
}
