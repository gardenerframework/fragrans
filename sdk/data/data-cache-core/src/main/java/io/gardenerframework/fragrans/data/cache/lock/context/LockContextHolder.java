package io.gardenerframework.fragrans.data.cache.lock.context;

import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/6/22 4:24 下午
 */
public interface LockContextHolder {
    /**
     * 获取上下文
     *
     * @param key 锁的key
     * @return 上下文，没有则返回null
     */
    @Nullable
    LockContext get(String key);

    /**
     * 设置上下文
     *
     * @param key     key
     * @param context 上下文
     */
    void set(String key, LockContext context);

    /**
     * 删除上下文
     *
     * @param key key
     */
    void remove(String key);
}
