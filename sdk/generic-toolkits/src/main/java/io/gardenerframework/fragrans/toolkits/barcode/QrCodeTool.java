package io.gardenerframework.fragrans.toolkits.barcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2021/12/22 9:40 下午
 */
public class QrCodeTool {
    /**
     * 生成二维码
     *
     * @param data            编码数据
     * @param size            大小
     * @param margin          间距
     * @param logoPath        图标地址
     * @param logoRatio       图标比例 0 - 0.99
     * @param codeColor       码的颜色
     * @param backgroundColor 背景颜色，一般为白色
     * @return base64的二维码
     * @throws WriterException 写像素矩阵发生问题
     * @throws IOException     读写发生问题
     */
    public String createSquareQrCode(
            String data,
            int size,
            int margin,
            String logoPath,
            float logoRatio,
            int codeColor,
            int backgroundColor
    ) throws WriterException, IOException {
        //生成绘制选项
        Map<EncodeHintType, Object> hint = new HashMap<>(3);
        // 设置误差修正
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置二维码空白区域大小
        hint.put(EncodeHintType.MARGIN, margin);
        // 设置字符集
        hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //生成点阵矩阵
        BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size, hint);
        BufferedImage qrCodeImage = new BufferedImage(bitMatrix.getWidth(), bitMatrix.getHeight(), BufferedImage.TYPE_INT_RGB);
        //向矩阵绘图
        for (int x = 0; x < bitMatrix.getWidth(); x++) {
            for (int y = 0; y < bitMatrix.getHeight(); y++) {
                qrCodeImage.setRGB(x, y, bitMatrix.get(x, y) ? codeColor : backgroundColor);
            }
        }
        //查看是否有必要添加logo
        if (logoRatio > 0.0F && StringUtils.hasText(logoPath)) {
            BufferedImage logoImage = ImageIO.read(new ClassPathResource(logoPath).getInputStream());
            // 计算logo缩放宽高
            int logoWidth = (int) (qrCodeImage.getWidth() * logoRatio);
            int logoHeight = (int) (qrCodeImage.getHeight() * logoRatio);

            // 缩放logo
            Image zoomImage = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);

            // 创建画笔
            Graphics2D g = qrCodeImage.createGraphics();
            // 将logo绘制到二维码上
            g.drawImage(zoomImage, (qrCodeImage.getWidth() - logoWidth) / 2, (qrCodeImage.getHeight() - logoHeight) / 2,
                    logoWidth, logoHeight, null);
            // 释放画笔
            g.dispose();
        }
        //转成base64字符串
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(qrCodeImage, "png", stream);
        return Base64.getEncoder().encodeToString(stream.toByteArray());
    }

    /**
     * 解析二维码
     *
     * @param image base64的二维码
     * @return 二维码的内容
     * @throws IOException       IOException
     * @throws NotFoundException NotFoundException
     */
    public String parseQrCode(String image) throws IOException, NotFoundException {
        BufferedImage qrCodeImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(image)));
        // 解码设置
        Map<DecodeHintType, Object> hints = new HashMap<>(1);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

        // 图像解析为亮度源
        BufferedImageLuminanceSource luminanceSource = new BufferedImageLuminanceSource(qrCodeImage);
        // 亮度源解析为二进制位图
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));

        // 二维码解码
        Result result = new MultiFormatReader().decode(bitmap, hints);

        // 解析后二维码内容
        return result.getText();
    }
}
