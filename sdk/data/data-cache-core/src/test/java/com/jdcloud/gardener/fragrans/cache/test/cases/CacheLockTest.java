package com.jdcloud.gardener.fragrans.cache.test.cases;

import com.jdcloud.gardener.fragrans.cache.test.CacheCoreTestApplication;
import com.jdcloud.gardener.fragrans.cache.test.NoScriptingTestRedisClient;
import com.jdcloud.gardener.fragrans.cache.test.TestRedisClient;
import com.jdcloud.gardener.fragrans.data.cache.lock.CacheLock;
import com.jdcloud.gardener.fragrans.data.cache.lock.context.LockContext;
import com.jdcloud.gardener.fragrans.data.cache.lock.context.ThreadLocalLockContextHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;

/**
 * @author ZhangHan
 * @date 2022/6/17 23:28
 */
@SpringBootTest(classes = CacheCoreTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("缓存锁测试")
public class CacheLockTest {
    @Autowired
    private RedisConnectionFactory connectionFactory;
    @LocalServerPort
    private int port;

    @Test
    public void smokeTest() throws IOException {
        CacheLock cacheLock = new CacheLock(new TestRedisClient(connectionFactory), new ThreadLocalLockContextHolder());
        cacheLock.lockThenRun("test", Duration.ofMinutes(1), () -> null);
        cacheLock = new CacheLock(new NoScriptingTestRedisClient(connectionFactory), new ThreadLocalLockContextHolder());
        cacheLock.lockThenRun("test", Duration.ofMinutes(1), () -> null);

    }

    @Test
    public void reenterTest() throws IOException {
        CacheLock cacheLock = new CacheLock(new TestRedisClient(connectionFactory), new ThreadLocalLockContextHolder());
        for (int i = 0; i < 100; i++) {
            //常规锁定和解锁的配对
            LockContext context = cacheLock.tryLock("test-reenter", Duration.ofMinutes(1));
            Assertions.assertNotNull(context);
            Assertions.assertFalse(context.isReentered());
            cacheLock.releaseLock("test-reenter", context);
        }
        //内嵌调用场景
        cacheLock.lockThenRun("test-reenter", Duration.ofMinutes(1), () -> {
            //声明另一个锁，查看不同锁实例和holder实例之间的重入性是否可以保证
            CacheLock lockInside;
            try {
                lockInside = new CacheLock(new TestRedisClient(connectionFactory), new ThreadLocalLockContextHolder());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            LockContext context = lockInside.tryLock("test-reenter", Duration.ofMinutes(2));
            Assertions.assertNotNull(context);
            //在同一线程内，虽然使用不同的锁实例和holder，但是对同样的key进行了上锁，因此视作重入
            Assertions.assertTrue(context.isReentered());
            lockInside.releaseLock("test-reenter", context);
            return null;
        });
        //锁后不开
        LockContext context = cacheLock.tryLock("test-reenter", Duration.ofMinutes(10));
        Assertions.assertNotNull(context);
        for (int i = 0; i < 100; i++) {
            LockContext contextReenter = cacheLock.tryLock("test-reenter", Duration.ofMinutes(10));
            Assertions.assertFalse(context.isReentered());
            Assertions.assertNotNull(contextReenter);
            Assertions.assertTrue(contextReenter.isReentered());
        }
        cacheLock.releaseLock("test-reenter", context);
    }

    @Test
    public void reenterOnWebThreadLocal() {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 100; i++) {
            restTemplate.getForObject("http://localhost:{port}/ServletCacheLockTestEndpoint/testLocal", void.class, port);
        }
    }

    @Test
    public void reenterOnWebRequest() {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 100; i++) {
            restTemplate.getForObject("http://localhost:{port}/ServletCacheLockTestEndpoint/testRequest", void.class, port);
        }
    }

    @Test
    public void reenterOnWebRequestWaiting() {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 2; i++) {
            restTemplate.getForObject("http://localhost:{port}/ServletCacheLockTestEndpoint/testRequestWaiting", void.class, port);
        }
    }
}
