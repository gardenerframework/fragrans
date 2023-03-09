package io.gardenerframework.fragrans.toolkits.test.cases;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import io.gardenerframework.fragrans.toolkits.barcode.QrCodeTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @author zhanghan30
 * @date 2021/12/22 10:07 下午
 */
@SpringBootTest
public class QrCodeToolTest {
    @Autowired
    private QrCodeTool qrCodeTool;

    @Test
    public void testWithEmptyLogo() throws IOException, WriterException, NotFoundException {
        String data = "https://some-site?q=1&a=2";
        String image = qrCodeTool.createSquareQrCode(
                data,
                300,
                1,
                null,
                0F,
                0,
                -1
        );
        String parsed = qrCodeTool.parseQrCode(image);
        Assertions.assertEquals(data, parsed);
    }

    @Test
    public void testWithLogo() throws IOException, WriterException, NotFoundException {
        String data = "https://some-site?q=1&a=2";
        String image = qrCodeTool.createSquareQrCode(
                data,
                300,
                1,
                "test.png",
                0.2F,
                0,
                -1
        );
        String parsed = qrCodeTool.parseQrCode(image);
        Assertions.assertEquals(data, parsed);
    }
}
