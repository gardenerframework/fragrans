package com.jdcloud.gardener.fragrans.validation.constraints.range;

import com.jdcloud.gardener.fragrans.validation.constraints.AbstractConstraintValidator;

import java.lang.annotation.Annotation;

/**
 * @author zhanghan30
 * @date 2022/6/13 8:46 下午
 */
public abstract class AbstractRangeConstraintValidator<A extends Annotation> extends AbstractConstraintValidator<A, Number> {
    /**
     * 判断类型是否兼容
     *
     * @param value      值
     * @param constraint 要求的范围
     */
    protected void checkTypeCompatibility(Number value, Number constraint) {
        if (!isCompatible(value, constraint)) {
            throw new IllegalArgumentException(String.format("incompatible type %s and %s", value.getClass(), constraint.getClass()));
        }
    }

    private boolean isCompatible(Number value, Number constraint) {
        return (isInteger(value) && isInteger(constraint))
                || (isFloat(value) && isFloat(constraint));
    }

    /**
     * 判断是否是整数
     *
     * @param target 目标
     * @return 是否是整数
     */
    protected boolean isInteger(Number target) {
        return target instanceof Byte || target instanceof Short || target instanceof Integer || target instanceof Long;
    }

    /**
     * 判断是否是小数
     *
     * @param target 目标
     * @return 是否是小数
     */
    protected boolean isFloat(Number target) {
        return target instanceof Float || target instanceof Double;
    }
}
