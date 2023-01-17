package com.jdcloud.gardener.fragrans.data.unique.test;

import com.jdcloud.gardener.fragrans.data.unique.HostIdGenerator;
import com.jdcloud.gardener.fragrans.data.unique.IpAddressHoseIdGenerator;
import com.jdcloud.gardener.fragrans.data.unique.UniqueIdGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author ZhangHan
 * @date 2021/8/23 20:23
 */
@SpringBootApplication
public class UniqueTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniqueTestApplication.class, args);
    }

    @Bean
    public IpAddressHoseIdGenerator ipAddressHoseIdGenerator() {
        return new IpAddressHoseIdGenerator();
    }

    @Bean
    public UniqueIdGenerator uniqueIdGenerator(HostIdGenerator hostIdGenerator) {
        return new UniqueIdGenerator(hostIdGenerator.getHostId());
    }
}
