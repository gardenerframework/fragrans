package com.jdcloud.gardener.fragrans.data.cache.lock.context;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author zhanghan30
 * @date 2022/6/22 4:38 下午
 */
public class ServletRequestLockContextHolder implements LockContextHolder {
    private String buildRequestAttributeKey(String key) {
        return String.format("%s.%s", ServletRequestLockContextHolder.class.getCanonicalName(), key);
    }

    @Nullable
    @Override
    public LockContext get(String key) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : (LockContext) requestAttributes.getAttribute(buildRequestAttributeKey(key), RequestAttributes.SCOPE_REQUEST);
    }

    @Override
    public void set(String key, LockContext context) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            requestAttributes.setAttribute(buildRequestAttributeKey(key), context, RequestAttributes.SCOPE_REQUEST);
        }
    }

    @Override
    public void remove(String key) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            requestAttributes.removeAttribute(buildRequestAttributeKey(key), RequestAttributes.SCOPE_REQUEST);
        }
    }
}
