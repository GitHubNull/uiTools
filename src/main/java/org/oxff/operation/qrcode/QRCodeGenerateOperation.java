package org.oxff.operation.qrcode;

import com.google.zxing.BarcodeFormat;
import org.oxff.operation.Operation;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.oxff.core.OperationCategory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成操作实现
 */
public class QRCodeGenerateOperation implements Operation {

    private static final int QR_CODE_SIZE = 300;
    private static final String IMAGE_FORMAT = "PNG";

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "请输入要生成二维码的内容";
        }

        try {
            // 生成二维码
            BufferedImage qrCodeImage = generateQRCodeImage(input.trim());

            // 将图片转换为Base64字符串
            String base64Image = imageToBase64(qrCodeImage);

            // 返回data URL格式的图片数据
            return "data:image/png;base64," + base64Image;

        } catch (WriterException e) {
            return "二维码生成失败: " + e.getMessage();
        } catch (IOException e) {
            return "图片转换失败: " + e.getMessage();
        } catch (Exception e) {
            return "生成二维码时发生错误: " + e.getMessage();
        }
    }

    /**
     * 生成二维码图片
     */
    private BufferedImage generateQRCodeImage(String text) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // 设置二维码参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // 设置边距

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 将图片转换为Base64字符串
     */
    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, IMAGE_FORMAT, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    @Override
    public OperationCategory getCategory() {
        return OperationCategory.QRCODE;
    }

    @Override
    public String getDisplayName() {
        return "生成二维码";
    }

    @Override
    public boolean returnsImage() {
        return true;
    }

    @Override
    public String getImageData(String input) {
        return execute(input);
    }
}