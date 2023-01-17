package com.jdcloud.gardener.fragrans.messages.test.cases;

import com.jdcloud.gardener.fragrans.messages.support.EnhancedMessageSourceSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

/**
 * 测试消息源的以下功能
 * 是否成功生成了bean
 * 是否资源名称全部正确
 *
 * @author ：zhanghan
 * @date ：2021/7/5 17:13
 */
@SpringBootTest
public class YamlMessageSourceTest {
    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @DisplayName("检测生成的MessageSource Bean类型")
    @Test
    public void testMessageSourceType() {
        Assertions.assertTrue(messageSource instanceof EnhancedMessageSourceSupport);
    }

    @DisplayName("检测注入的名称")
    @Test
    public void testMessageSourceBasenames() {
        EnhancedMessageSourceSupport messageSource = (EnhancedMessageSourceSupport) this.messageSource;
        Assertions.assertTrue(messageSource.getBasenameSet().containsAll(Arrays.asList("test-1", "test-2", "test-3")));
    }

    @DisplayName("检测国际化文本")
    @Test
    public void testGetMessages() {
        for (int i = 1; i <= 3; i++) {
            String message = messageSource.getMessage("test" + i, null, Locale.getDefault());
            Assertions.assertEquals("来自测试" + i, message);
        }
    }

    @Test
    @DisplayName("检测不存在的文件")
    public void testNoMessage() {
        Assertions.assertThrows(
                NoSuchMessageException.class,
                () -> messageSource.getMessage(UUID.randomUUID().toString(), null, Locale.getDefault())
        );
    }
}
