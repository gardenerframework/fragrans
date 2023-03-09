package io.gardenerframework.fragrans.toolkits.configuration;

import io.gardenerframework.fragrans.toolkits.barcode.QrCodeTool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2021/12/22 9:39 下午
 */
@Configuration
public class ToolkitsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(QrCodeTool.class)
    public QrCodeTool qrCodeTool() {
        return new QrCodeTool();
    }
}
