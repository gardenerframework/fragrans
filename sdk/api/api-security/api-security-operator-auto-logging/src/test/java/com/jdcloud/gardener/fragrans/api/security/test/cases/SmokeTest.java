package com.jdcloud.gardener.fragrans.api.security.test.cases;

import com.jdcloud.gardener.fragrans.api.security.test.SecurityOperatorAutoLoggingTestApplication;
import com.jdcloud.gardener.fragrans.log.GenericOperationLogger;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Register;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhanghan30
 * @date 2021/12/1 5:03 下午
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SecurityOperatorAutoLoggingTestApplication.class})
@Slf4j
public class SmokeTest {
    @Autowired
    private GenericOperationLogger logger;
    @LocalServerPort
    private int port;

    @Test
    public void test() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject("http://localhost:{port}", String.class, port);
    }

    @Test
    public void nonRequestContextTest() {
        logger.info(log, GenericOperationLogContent.builder().what(SmokeTest.class).operation(new Register()).state(new Done()).build(), null);
    }
}
