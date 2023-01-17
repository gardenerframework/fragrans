package com.jdcloud.gardener.fragrans.event.test;

import com.jdcloud.gardener.fragrans.event.test.cases.KafkaDomainEventTest;
import com.jdcloud.gardener.fragrans.data.unique.HostIdGenerator;
import com.jdcloud.gardener.fragrans.data.unique.IpAddressHoseIdGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author zhanghan30
 * @date 2021/11/4 10:48 下午
 */
@SpringBootApplication
@EnableAsync
public class KafkaDomainEventTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaDomainEventTestApplication.class, args);
    }

    @Bean
    public HostIdGenerator hostIdGenerator() {
        return new IpAddressHoseIdGenerator();
    }

    @Bean
    public KafkaDomainEventTest.Consumer consumer() {
        return new KafkaDomainEventTest.Consumer();
    }
}
