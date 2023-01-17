package com.jdcloud.gardener.fragrans.data.cache.client;

import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 指明只有redis支持的操作
 *
 * @author zhanghan30
 * @date 2022/5/18 5:28 下午
 */
public interface RedisOperation {

    /**
     * 是否支持lua脚本
     *
     * @return 是否支持
     */
    default boolean supportLuaScript() {
        return false;
    }

    /**
     * 脚本hash是否已经存在
     *
     * @param scriptHash 脚本hash
     * @return 是否已经被加载过了
     */
    default boolean scriptExists(String scriptHash) {
        throw new UnsupportedOperationException();
    }

    /**
     * 加载lua脚本
     *
     * @param script 脚本内容
     * @return 脚本hash值
     */
    default String loadLuaScript(String script) {
        throw new UnsupportedOperationException();
    }

    /**
     * 读取lua脚本并返回脚本的hash值
     *
     * @param path 路径
     * @return hash值，用于执行脚本
     * @throws IOException 无法读取文件或内容
     */
    default String loadLuaScriptFile(String path) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(path).getInputStream()));
        StringBuilder stringBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
            stringBuffer.append("\n");
        }
        return this.loadLuaScript(stringBuffer.toString());
    }

    /**
     * 执行脚本
     * <p>
     * 返回字节流模式
     *
     * @param scriptHash  脚本hash
     * @param numberKeys  有多少个key
     * @param keysAndArgs 实际的传参数
     * @return 执行结果
     */
    @Nullable
    default byte[] executeScript(String scriptHash, int numberKeys, byte[]... keysAndArgs) {
        return executeScript(scriptHash, byte[].class, numberKeys, keysAndArgs);
    }

    /**
     * 执行脚本
     * <p>
     * 返回给定的类型模式
     * <p>
     * 警告: 一般key的类型都是string，这时候调用上面的方法就行了
     * <p>
     * {@link #executeScript(String, int, byte[]...)}
     * <p>
     * 下面这个是给script返回其它类型用的
     *
     * @param scriptHash  脚本hash
     * @param numberKeys  有多少个key
     * @param keysAndArgs 实际的传参数
     * @return 执行结果
     */
    @Nullable
    default <T> T executeScript(String scriptHash, Class<T> resultType, int numberKeys, byte[]... keysAndArgs) {
        throw new UnsupportedOperationException();
    }

    /**
     * 設置hash
     *
     * @param key   缓存key
     * @param field 字段
     * @param value 值
     */
    void hset(String key, String field, byte[] value);

    /**
     * 設置hash(当不存在时)
     *
     * @param key   缓存key
     * @param field 字段
     * @param value 值
     */
    boolean hsetnx(String key, String field, byte[] value);


    /**
     * 返回hash表指定字段
     *
     * @param key   缓存key
     * @param field 字段
     * @return 值
     */
    @Nullable
    byte[] hget(String key, String field);

    /**
     * 删除指定的hset 字段
     *
     * @param key   缓存key
     * @param field 字段
     */
    void hdelete(String key, String field);
}
