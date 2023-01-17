package com.jdcloud.gardener.fragrans.cache.test;

import com.jdcloud.gardener.fragrans.data.cache.lock.CacheLock;
import com.jdcloud.gardener.fragrans.data.cache.lock.context.LockContext;
import com.jdcloud.gardener.fragrans.data.cache.lock.context.ServletRequestLockContextHolder;
import com.jdcloud.gardener.fragrans.data.cache.lock.context.ThreadLocalLockContextHolder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * @author zhanghan30
 * @date 2022/6/22 4:48 下午
 */
@RestController
@RequestMapping("/ServletCacheLockTestEndpoint")
@Component
@RequiredArgsConstructor
public class ServletCacheLockTestEndpoint {
    private final RedisConnectionFactory connectionFactory;
    private boolean testLocalFirstTouch = true;
    private boolean testRequestFirstTouch = true;
    private boolean testRequestWaitingFirstTouch = true;

    @GetMapping("/testLocal")
    public void testLocal() throws IOException {
        CacheLock cacheLock = new CacheLock(new TestRedisClient(connectionFactory), new ThreadLocalLockContextHolder());
        LockContext testLocal = cacheLock.tryLock("testLocal", Duration.ofSeconds(10));
        Assertions.assertNotNull(testLocal);
        if (testLocalFirstTouch) {
            Assertions.assertFalse(testLocal.isReentered());
            testLocalFirstTouch = false;
        } else {
            Assertions.assertTrue(testLocal.isReentered());
        }
    }

    @GetMapping("/testRequest")
    public void testRequest() throws IOException {
        CacheLock cacheLock = new CacheLock(new TestRedisClient(connectionFactory), new ServletRequestLockContextHolder());
        LockContext context = cacheLock.tryLock("testRequest", Duration.ofSeconds(10));
        if (testRequestFirstTouch) {
            Assertions.assertNotNull(context);
            Assertions.assertFalse(context.isReentered());
            testRequestFirstTouch = false;
        } else {
            //这是因为非重入，然而锁还有10秒未释放，所以try失败
            Assertions.assertNull(context);
        }
    }

    @GetMapping("/testRequestWaiting")
    public void testRequestWaiting() throws IOException {
        CacheLock cacheLock = new CacheLock(new TestRedisClient(connectionFactory), new ServletRequestLockContextHolder());
        Instant start = Instant.now();
        LockContext context = cacheLock.lock("testRequestWaiting", Duration.ofSeconds(10));
        if (testRequestWaitingFirstTouch) {
            Assertions.assertNotNull(context);
            Assertions.assertFalse(context.isReentered());
            testRequestWaitingFirstTouch = false;
        } else {
            Assertions.assertTrue(Duration.between(start, Instant.now()).getSeconds() > 5);
        }
    }
}
