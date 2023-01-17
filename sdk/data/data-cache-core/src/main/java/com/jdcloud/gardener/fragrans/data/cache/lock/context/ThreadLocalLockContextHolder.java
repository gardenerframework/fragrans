package com.jdcloud.gardener.fragrans.data.cache.lock.context;

import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhanghan30
 * @date 2022/6/22 4:26 下午
 */
public class ThreadLocalLockContextHolder implements LockContextHolder {
    private static final ThreadLocal<Map<String, LockContext>> LOCK_CONTEXT = ThreadLocal.withInitial(ConcurrentHashMap::new);

    @Nullable
    @Override
    public LockContext get(String key) {
        return LOCK_CONTEXT.get().get(key);
    }

    @Override
    public void set(String key, LockContext context) {
        LOCK_CONTEXT.get().put(key, context);
    }

    @Override
    public void remove(String key) {
        LOCK_CONTEXT.get().remove(key);
    }
}
