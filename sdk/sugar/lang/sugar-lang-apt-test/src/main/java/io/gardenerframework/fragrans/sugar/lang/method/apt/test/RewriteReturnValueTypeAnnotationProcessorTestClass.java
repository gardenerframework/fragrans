package io.gardenerframework.fragrans.sugar.lang.method.apt.test;

import io.gardenerframework.fragrans.sugar.lang.method.annotation.KeepReturnValueType;
import io.gardenerframework.fragrans.sugar.lang.method.annotation.RewriteReturnValueType;
import io.gardenerframework.fragrans.sugar.lang.method.apt.test.nest.NestObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Type;

/**
 * @author zhanghan30
 * @date 2022/9/14 5:02 下午
 */
@RewriteReturnValueType(NestObject.class)
public class RewriteReturnValueTypeAnnotationProcessorTestClass {
    @Getter(onMethod = @__(@KeepReturnValueType))
    private String field;

    public void method() {
        return new NestObject();
    }

    @RewriteReturnValueType(int.class)
    public void method2() {
        return 1;
    }

    @KeepReturnValueType
    public String method3() {
        return getField();
    }

    /**
     * 以下部分的代码之前会报错
     */
    @SuppressWarnings("rawtypes")
    @AllArgsConstructor
    private class AccountAggregationPrincipalsColumnTypeHandler {
        private Class<?> clazz;

        protected Type getTypeReference() {
            try {
                Class aggregationType = clazz;
                return new Type() {
                    @Override
                    public String getTypeName() {
                        return Type.super.getTypeName();
                    }
                };
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
