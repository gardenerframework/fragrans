package io.gardenerframework.fragrans.sugar.trait.apt.test;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

/**
 * @author chris
 * @date 2023/10/27
 */
@Trait
public interface TestInterfaceWithFields {
    String a = "";
    final String b = "";
}
