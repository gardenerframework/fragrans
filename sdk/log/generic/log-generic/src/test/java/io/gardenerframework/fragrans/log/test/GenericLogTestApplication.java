package io.gardenerframework.fragrans.log.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author zhanghan30
 * @date 2022/6/9 2:12 下午
 */
@SpringBootApplication
public class GenericLogTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(GenericLogTestApplication.class, args);
    }

    @Bean
    public Long whatever() {
        return -1L;
    }
}
