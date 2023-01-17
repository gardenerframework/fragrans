package io.gardenerframework.fragrans.log.test;

import io.gardenerframework.fragrans.log.event.LogEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

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

    @EventListener
    public void onLogEvent(LogEvent event) {

    }
}
