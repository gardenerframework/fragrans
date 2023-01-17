package io.gardenerframework.fragrans.sugar.lang.method.apt.test;

import io.gardenerframework.fragrans.sugar.lang.method.annotation.RewriteReturnValueType;
import io.gardenerframework.fragrans.sugar.lang.method.apt.test.base.TestBaseClass;
import io.gardenerframework.fragrans.sugar.lang.method.apt.test.interfaces.TestInterface;
import io.gardenerframework.fragrans.sugar.lang.method.apt.test.interfaces.TestTemplateInterface;
import io.gardenerframework.fragrans.sugar.lang.method.apt.test.nest.NestObject;
import io.gardenerframework.fragrans.sugar.lang.type.annotation.Uninherit;

/**
 * @author zhanghan30
 * @date 2022/9/16 3:31 下午
 */
@RewriteReturnValueType(String.class)
@Uninherit({TestInterface.class, TestBaseClass.class, TestTemplateInterface.class})
public class RewriteReturnValueTypeAnnotationProcessorTestClassWithInterface<C> extends TestBaseClass<C> implements TestInterface, TestTemplateInterface<C> {
    private final NestObject nestObject = testPrivate();

    @Override
    public void test() {
        return "";
    }

    @Override
    public void testClass() {
        return "";
    }

    @Override
    public C testTemplate() {
        return "";
    }

    private NestObject testPrivate() {
        return new NestObject();
    }
}
