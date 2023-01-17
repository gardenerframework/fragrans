package io.gardenerframework.fragrans.messages.configuration.basename;

import java.util.Set;

/**
 * 用来提供message所需的basename
 *
 * @author ZhangHan
 * @date 2022/6/10 9:48
 */
@FunctionalInterface
public interface BasenameProvider {
    /**
     * 给出basename
     *
     * @return basename
     * @throws Exception 出现问题
     */
    Set<String> getBasenames() throws Exception;
}
