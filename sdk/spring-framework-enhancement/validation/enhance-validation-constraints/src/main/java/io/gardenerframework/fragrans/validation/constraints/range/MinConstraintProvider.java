package io.gardenerframework.fragrans.validation.constraints.range;

/**
 * @author zhanghan30
 * @date 2022/6/13 7:28 下午
 */
@FunctionalInterface
public interface MinConstraintProvider {
    /**
     * 给出最小值
     *
     * @return 最小值
     */
    Number getMin();
}
